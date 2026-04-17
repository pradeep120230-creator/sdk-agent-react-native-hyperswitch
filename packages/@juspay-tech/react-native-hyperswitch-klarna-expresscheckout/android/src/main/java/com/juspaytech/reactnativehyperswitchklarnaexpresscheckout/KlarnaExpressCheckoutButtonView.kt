package com.juspaytech.reactnativehyperswitchklarnaexpresscheckout

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.FrameLayout
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.events.RCTEventEmitter
import com.klarna.mobile.sdk.api.KlarnaEnvironment
import com.klarna.mobile.sdk.api.KlarnaRegion
import com.klarna.mobile.sdk.api.KlarnaTheme
import com.klarna.mobile.sdk.api.KlarnaLoggingLevel
import com.klarna.mobile.sdk.api.checkout.express.KlarnaExpressCheckoutButton
import com.klarna.mobile.sdk.api.checkout.express.KlarnaExpressCheckoutButtonAuthorizationResponse
import com.klarna.mobile.sdk.api.checkout.express.KlarnaExpressCheckoutButtonCallback
import com.klarna.mobile.sdk.api.checkout.express.KlarnaExpressCheckoutButtonOptions
import com.klarna.mobile.sdk.api.checkout.express.KlarnaExpressCheckoutButtonStyleConfiguration
import com.klarna.mobile.sdk.api.checkout.express.KlarnaExpressCheckoutError
import com.klarna.mobile.sdk.api.checkout.express.KlarnaExpressCheckoutSessionOptions
import com.klarna.mobile.sdk.api.component.button.KlarnaButtonShape
import com.klarna.mobile.sdk.api.component.button.KlarnaButtonStyle
import com.klarna.mobile.sdk.api.component.button.KlarnaButtonTheme

class KlarnaExpressCheckoutButtonView(private val reactContext: ThemedReactContext) :
    FrameLayout(reactContext) {

    var clientToken: String? = null
    var sessionData: String? = null
    var autoFinalize: Boolean = true
    var collectShippingAddress: Boolean = false
    var buttonLocale: String = "en-US"
    var environment: String = "production"
    var region: String = "NA"
    var theme: String = "dark"
    var buttonShape: String = "rounded_rect"
    var buttonStyleValue: String = "filled"
    var returnUrlValue: String? = null
    var loggingLevel: String = "off"

    private var klarnaButton: KlarnaExpressCheckoutButton? = null

    private val callback = object : KlarnaExpressCheckoutButtonCallback {
        override fun onAuthorized(
            view: KlarnaExpressCheckoutButton,
            response: KlarnaExpressCheckoutButtonAuthorizationResponse
        ) {
            val payload: WritableMap = Arguments.createMap().apply {
                putBoolean("approved", response.approved)
                putBoolean("showForm", response.showForm)
                putBoolean("finalizeRequired", response.finalizeRequired)
                response.authorizationToken?.let { putString("authorizationToken", it) }
                response.clientToken?.let { putString("clientToken", it) }
                response.sessionId?.let { putString("sessionId", it) }
                response.collectedShippingAddress?.let { putString("collectedShippingAddress", it) }
                response.merchantReference1?.let { putString("merchantReference1", it) }
                response.merchantReference2?.let { putString("merchantReference2", it) }
            }
            dispatchEvent("onAuthorized", payload)
        }

        override fun onError(view: KlarnaExpressCheckoutButton, error: KlarnaExpressCheckoutError) {
            val payload: WritableMap = Arguments.createMap().apply {
                putString("name", error.name)
                putString("message", error.message)
                putBoolean("isFatal", error.isFatal)
                error.sessionId?.let { putString("sessionId", it) }
            }
            dispatchEvent("onError", payload)
        }
    }

    fun renderButton() {
        Handler(Looper.getMainLooper()).post {
            val token = clientToken
            if (token.isNullOrBlank()) {
                return@post
            }

            removeExistingButton()

            val sessionOptions = KlarnaExpressCheckoutSessionOptions.ServerSideSession(
                clientToken = token,
                sessionData = sessionData,
                autoFinalize = autoFinalize,
                collectShippingAddress = collectShippingAddress
            )

            val styleConfiguration = KlarnaExpressCheckoutButtonStyleConfiguration(
                theme = mapButtonTheme(theme),
                shape = mapButtonShape(buttonShape),
                style = mapButtonStyle(buttonStyleValue)
            )

            val options = KlarnaExpressCheckoutButtonOptions(
                sessionOptions = sessionOptions,
                callback = callback,
                locale = buttonLocale,
                styleConfiguration = styleConfiguration,
                environment = mapEnvironment(environment, region),
                region = mapRegion(region),
                theme = mapFlowTheme(theme),
                loggingLevel = mapLoggingLevel(loggingLevel)
            )

            val hostContext: Context = reactContext.currentActivity ?: reactContext
            klarnaButton = KlarnaExpressCheckoutButton(hostContext, options).also { btn ->
                btn.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                addView(btn)
            }
        }
    }

    private fun removeExistingButton() {
        klarnaButton?.let { removeView(it) }
        klarnaButton = null
    }

    private fun dispatchEvent(name: String, payload: WritableMap) {
        reactContext
            .getJSModule(RCTEventEmitter::class.java)
            .receiveEvent(id, name, payload)
    }

    private fun mapButtonTheme(value: String): KlarnaButtonTheme = when (value.lowercase()) {
        "light" -> KlarnaButtonTheme.LIGHT
        "auto" -> KlarnaButtonTheme.AUTO
        else -> KlarnaButtonTheme.DARK
    }

    private fun mapButtonShape(value: String): KlarnaButtonShape = when (value.lowercase()) {
        "rectangle" -> KlarnaButtonShape.RECTANGLE
        "pill" -> KlarnaButtonShape.PILL
        else -> KlarnaButtonShape.ROUNDED_RECT
    }

    private fun mapButtonStyle(value: String): KlarnaButtonStyle = when (value.lowercase()) {
        "outlined" -> KlarnaButtonStyle.OUTLINED
        else -> KlarnaButtonStyle.FILLED
    }

    private fun mapFlowTheme(value: String): KlarnaTheme = when (value.lowercase()) {
        "dark" -> KlarnaTheme.DARK
        "auto" -> KlarnaTheme.AUTOMATIC
        else -> KlarnaTheme.LIGHT
    }

    private fun mapEnvironment(env: String, region: String): KlarnaEnvironment {
        val upperRegion = region.uppercase()
        return when (env.lowercase()) {
            "playground" -> when (upperRegion) {
                "EU" -> KlarnaEnvironment.PLAYGROUND_EU
                "OC" -> KlarnaEnvironment.PLAYGROUND_OC
                else -> KlarnaEnvironment.PLAYGROUND_NA
            }
            else -> when (upperRegion) {
                "EU" -> KlarnaEnvironment.PRODUCTION_EU
                "OC" -> KlarnaEnvironment.PRODUCTION_OC
                else -> KlarnaEnvironment.PRODUCTION_NA
            }
        }
    }

    private fun mapRegion(value: String): KlarnaRegion = when (value.uppercase()) {
        "EU" -> KlarnaRegion.EU
        "OC" -> KlarnaRegion.OC
        else -> KlarnaRegion.NA
    }

    private fun mapLoggingLevel(value: String): KlarnaLoggingLevel = when (value.lowercase()) {
        "error" -> KlarnaLoggingLevel.Error
        "verbose" -> KlarnaLoggingLevel.Verbose
        else -> KlarnaLoggingLevel.Off
    }
}
