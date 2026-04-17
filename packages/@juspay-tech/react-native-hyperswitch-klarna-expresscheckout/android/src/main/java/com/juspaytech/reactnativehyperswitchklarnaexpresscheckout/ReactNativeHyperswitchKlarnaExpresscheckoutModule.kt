package com.juspaytech.reactnativehyperswitchklarnaexpresscheckout

import android.content.Intent
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.WritableMap

class ReactNativeHyperswitchKlarnaExpresscheckoutModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String = NAME

    @ReactMethod
    fun finalize(clientToken: String, callback: Callback) {
        KlarnaExpressCheckoutPendingResult.setFinalizeCallback(
            object : KlarnaExpressCheckoutPendingResult.FinalizeCallback {
                override fun onSuccess(
                    approved: Boolean,
                    showForm: Boolean,
                    finalizeRequired: Boolean,
                    authorizationToken: String?,
                    clientToken: String?,
                    sessionId: String?,
                    collectedShippingAddress: String?,
                    merchantReference1: String?,
                    merchantReference2: String?
                ) {
                    val map: WritableMap = Arguments.createMap().apply {
                        putBoolean("approved", approved)
                        putBoolean("showForm", showForm)
                        putBoolean("finalizeRequired", finalizeRequired)
                        authorizationToken?.let { putString("authorizationToken", it) }
                        clientToken?.let { putString("clientToken", it) }
                        sessionId?.let { putString("sessionId", it) }
                        collectedShippingAddress?.let { putString("collectedShippingAddress", it) }
                        merchantReference1?.let { putString("merchantReference1", it) }
                        merchantReference2?.let { putString("merchantReference2", it) }
                    }
                    callback.invoke(map)
                }

                override fun onError(name: String, message: String, isFatal: Boolean, sessionId: String?) {
                    val map: WritableMap = Arguments.createMap().apply {
                        putBoolean("approved", false)
                        putBoolean("showForm", false)
                        putBoolean("finalizeRequired", false)
                        putString("errorName", name)
                        putString("errorMessage", message)
                        putBoolean("isFatal", isFatal)
                        sessionId?.let { putString("sessionId", it) }
                    }
                    callback.invoke(map)
                }
            }
        )

        val intent = Intent(reactApplicationContext, KlarnaExpressCheckoutHostActivity::class.java)
        intent.putExtra(KlarnaExpressCheckoutHostActivity.EXTRA_CLIENT_TOKEN, clientToken)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        reactApplicationContext.startActivity(intent)
    }

    companion object {
        const val NAME = "HyperswitchKlarnaExpressCheckout"
    }
}
