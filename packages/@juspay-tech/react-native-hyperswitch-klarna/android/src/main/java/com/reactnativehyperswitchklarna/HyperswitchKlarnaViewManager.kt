package com.reactnativehyperswitchklarna

import android.os.Handler
import android.os.Looper
import android.view.View
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.WritableMap
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.events.RCTEventEmitter
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentView
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentViewCallback
import com.klarna.mobile.sdk.api.KlarnaMobileSDKError

class HyperswitchKlarnaViewManager : SimpleViewManager<KlarnaPaymentView>() {

    companion object {
        const val NAME = "HyperswitchKlarnaPaymentView"
        const val COMMAND_INITIALIZE = 1
        const val COMMAND_LOAD = 2
        const val COMMAND_AUTHORIZE = 3
    }

    private val mainHandler = Handler(Looper.getMainLooper())

    override fun getName(): String = NAME

    override fun createViewInstance(reactContext: ThemedReactContext): KlarnaPaymentView {
        val view = KlarnaPaymentView(reactContext, "klarna")
        val callback = object : KlarnaPaymentViewCallback {
            override fun onInitialized(view: KlarnaPaymentView) {
                emitEvent(reactContext, view, "onInitialized", Arguments.createMap())
            }

            override fun onLoaded(view: KlarnaPaymentView) {
                emitEvent(reactContext, view, "onLoaded", Arguments.createMap())
            }

            override fun onLoadPaymentReview(view: KlarnaPaymentView, showForm: Boolean) {}

            override fun onAuthorized(
                view: KlarnaPaymentView,
                approved: Boolean,
                authToken: String?,
                finalizeRequired: Boolean?
            ) {
                val payload = Arguments.createMap().apply {
                    putBoolean("authorized", true)
                    putBoolean("approved", approved)
                    if (authToken != null) putString("authToken", authToken)
                    if (finalizeRequired != null) putBoolean("finalizeRequired", finalizeRequired)
                }
                emitEvent(reactContext, view, "onAuthorized", payload)
            }

            override fun onReauthorized(
                view: KlarnaPaymentView,
                approved: Boolean,
                authToken: String?
            ) {
                val payload = Arguments.createMap().apply {
                    putBoolean("authorized", true)
                    putBoolean("approved", approved)
                    if (authToken != null) putString("authToken", authToken)
                }
                emitEvent(reactContext, view, "onAuthorized", payload)
            }

            override fun onFinalized(
                view: KlarnaPaymentView,
                approved: Boolean,
                authToken: String?
            ) {
                val payload = Arguments.createMap().apply {
                    putBoolean("authorized", true)
                    putBoolean("approved", approved)
                    if (authToken != null) putString("authToken", authToken)
                }
                emitEvent(reactContext, view, "onAuthorized", payload)
            }

            override fun onErrorOccurred(view: KlarnaPaymentView, error: KlarnaMobileSDKError) {
                val payload = Arguments.createMap().apply {
                    putBoolean("authorized", false)
                    putBoolean("approved", false)
                    putString("errorMessage", error.name ?: error.message ?: "KlarnaError")
                }
                emitEvent(reactContext, view, "onAuthorized", payload)
            }
        }
        view.registerPaymentViewCallback(callback)
        return view
    }

    @ReactProp(name = "category")
    fun setCategory(view: KlarnaPaymentView, category: String?) {
        if (!category.isNullOrEmpty()) {
            view.category = category
        }
    }

    @ReactProp(name = "returnURL")
    fun setReturnURL(view: KlarnaPaymentView, returnURL: String?) {
        if (!returnURL.isNullOrEmpty()) {
            view.returnURL = returnURL
        }
    }

    override fun getCommandsMap(): Map<String, Int> = mapOf(
        "initialize" to COMMAND_INITIALIZE,
        "load" to COMMAND_LOAD,
        "authorize" to COMMAND_AUTHORIZE
    )

    override fun receiveCommand(view: KlarnaPaymentView, commandId: Int, args: ReadableArray?) {
        mainHandler.post {
            when (commandId) {
                COMMAND_INITIALIZE -> {
                    val clientToken = args?.getString(0) ?: return@post
                    val returnUrl = if (args.size() > 1 && !args.isNull(1)) args.getString(1) else null
                    if (!returnUrl.isNullOrEmpty()) {
                        view.returnURL = returnUrl
                    }
                    view.initialize(clientToken)
                }
                COMMAND_LOAD -> view.load(null)
                COMMAND_AUTHORIZE -> view.authorize(autoFinalize = true, sessionData = null)
            }
        }
    }

    override fun getExportedCustomDirectEventTypeConstants(): Map<String, Any> = MapBuilder.of(
        "onInitialized", MapBuilder.of("registrationName", "onInitialized"),
        "onLoaded", MapBuilder.of("registrationName", "onLoaded"),
        "onAuthorized", MapBuilder.of("registrationName", "onAuthorized")
    )

    private fun emitEvent(
        context: ReactContext,
        view: View,
        eventName: String,
        payload: WritableMap
    ) {
        context.getJSModule(RCTEventEmitter::class.java)
            .receiveEvent(view.id, eventName, payload)
    }
}
