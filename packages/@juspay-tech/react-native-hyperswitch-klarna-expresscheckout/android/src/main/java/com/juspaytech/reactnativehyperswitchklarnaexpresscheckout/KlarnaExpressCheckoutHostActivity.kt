package com.juspaytech.reactnativehyperswitchklarnaexpresscheckout

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentView
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentViewCallback
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentsSDKError

class KlarnaExpressCheckoutHostActivity : Activity(), KlarnaPaymentViewCallback {

    private var paymentView: KlarnaPaymentView? = null
    private var clientToken: String? = null
    private var returnUrl: String = ""
    private var callbackInvoked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(0, 0)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        super.onCreate(savedInstanceState)

        clientToken = intent?.getStringExtra(EXTRA_CLIENT_TOKEN)
        returnUrl = intent?.getStringExtra(EXTRA_RETURN_URL) ?: ""

        if (clientToken.isNullOrBlank()) {
            finishWithError("InvalidClientToken", "Missing clientToken for finalize", true)
            return
        }

        val container = FrameLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(0, 0)
        }
        setContentView(container)

        Handler(Looper.getMainLooper()).post {
            try {
                val view = KlarnaPaymentView(this)
                view.addCategory(CATEGORY_PAY_LATER)
                view.addPaymentViewCallback(this)
                container.addView(view)
                paymentView = view
                view.initialize(clientToken!!, returnUrl)
            } catch (t: Throwable) {
                finishWithError("FinalizeSetupFailed", t.message ?: "Setup failed", true)
            }
        }
    }

    override fun onInitialized(view: KlarnaPaymentView) {
        Handler(Looper.getMainLooper()).post {
            runCatching { view.load(null) }
                .onFailure {
                    finishWithError("FinalizeLoadFailed", it.message ?: "Load failed", true)
                }
        }
    }

    override fun onLoaded(view: KlarnaPaymentView) {
        Handler(Looper.getMainLooper()).post {
            runCatching { view.finalize(null) }
                .onFailure {
                    finishWithError("FinalizeCallFailed", it.message ?: "Finalize failed", true)
                }
        }
    }

    override fun onFinalized(view: KlarnaPaymentView, approved: Boolean, authToken: String?) {
        if (callbackInvoked) return
        callbackInvoked = true
        KlarnaExpressCheckoutPendingResult.notifyFinalizeSuccess(
            approved = approved,
            showForm = false,
            finalizeRequired = false,
            authorizationToken = authToken,
            clientToken = null,
            sessionId = null,
            collectedShippingAddress = null,
            merchantReference1 = null,
            merchantReference2 = null
        )
        finish()
        overridePendingTransition(0, 0)
    }

    override fun onAuthorized(
        view: KlarnaPaymentView,
        approved: Boolean,
        authToken: String?,
        finalizeRequired: Boolean
    ) {}

    override fun onReauthorized(view: KlarnaPaymentView, approved: Boolean, authToken: String?) {}

    override fun onLoadPaymentReview(view: KlarnaPaymentView, showForm: Boolean) {}

    override fun onErrorOccurred(view: KlarnaPaymentView, error: KlarnaPaymentsSDKError) {
        finishWithError(error.name, error.message, error.isFatal)
    }

    private fun finishWithError(name: String, message: String, isFatal: Boolean) {
        if (callbackInvoked) return
        callbackInvoked = true
        KlarnaExpressCheckoutPendingResult.notifyFinalizeError(name, message, isFatal, null)
        finish()
        overridePendingTransition(0, 0)
    }

    override fun onDestroy() {
        if (!callbackInvoked) {
            callbackInvoked = true
            KlarnaExpressCheckoutPendingResult.notifyFinalizeError(
                "FinalizeCancelled",
                "Finalize flow was dismissed before completion",
                false,
                null
            )
        }
        super.onDestroy()
    }

    companion object {
        const val EXTRA_CLIENT_TOKEN = "clientToken"
        const val EXTRA_RETURN_URL = "returnUrl"
        private const val CATEGORY_PAY_LATER = "pay_later"
    }
}
