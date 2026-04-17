package com.juspaytech.reactnativehyperswitchklarnaexpresscheckout

import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp

class KlarnaExpressCheckoutButtonManager : SimpleViewManager<KlarnaExpressCheckoutButtonView>() {

    override fun getName(): String = REACT_CLASS

    override fun createViewInstance(context: ThemedReactContext): KlarnaExpressCheckoutButtonView =
        KlarnaExpressCheckoutButtonView(context)

    override fun onAfterUpdateTransaction(view: KlarnaExpressCheckoutButtonView) {
        super.onAfterUpdateTransaction(view)
        view.renderButton()
    }

    override fun getExportedCustomDirectEventTypeConstants(): Map<String, Any> {
        return MapBuilder.of(
            "onAuthorized",
            MapBuilder.of("registrationName", "onAuthorized"),
            "onError",
            MapBuilder.of("registrationName", "onError")
        )
    }

    @ReactProp(name = "clientToken")
    fun setClientToken(view: KlarnaExpressCheckoutButtonView, value: String?) {
        view.clientToken = value
    }

    @ReactProp(name = "sessionData")
    fun setSessionData(view: KlarnaExpressCheckoutButtonView, value: String?) {
        view.sessionData = value
    }

    @ReactProp(name = "autoFinalize", defaultBoolean = true)
    fun setAutoFinalize(view: KlarnaExpressCheckoutButtonView, value: Boolean) {
        view.autoFinalize = value
    }

    @ReactProp(name = "collectShippingAddress", defaultBoolean = false)
    fun setCollectShippingAddress(view: KlarnaExpressCheckoutButtonView, value: Boolean) {
        view.collectShippingAddress = value
    }

    @ReactProp(name = "locale")
    fun setLocale(view: KlarnaExpressCheckoutButtonView, value: String?) {
        view.buttonLocale = value ?: "en-US"
    }

    @ReactProp(name = "environment")
    fun setEnvironment(view: KlarnaExpressCheckoutButtonView, value: String?) {
        view.environment = value ?: "production"
    }

    @ReactProp(name = "region")
    fun setRegion(view: KlarnaExpressCheckoutButtonView, value: String?) {
        view.region = value ?: "NA"
    }

    @ReactProp(name = "theme")
    fun setTheme(view: KlarnaExpressCheckoutButtonView, value: String?) {
        view.theme = value ?: "dark"
    }

    @ReactProp(name = "buttonShape")
    fun setButtonShape(view: KlarnaExpressCheckoutButtonView, value: String?) {
        view.buttonShape = value ?: "rounded_rect"
    }

    @ReactProp(name = "buttonStyleValue")
    fun setButtonStyleValue(view: KlarnaExpressCheckoutButtonView, value: String?) {
        view.buttonStyleValue = value ?: "filled"
    }

    @ReactProp(name = "returnUrl")
    fun setReturnUrl(view: KlarnaExpressCheckoutButtonView, value: String?) {
        view.returnUrlValue = value
    }

    @ReactProp(name = "loggingLevel")
    fun setLoggingLevel(view: KlarnaExpressCheckoutButtonView, value: String?) {
        view.loggingLevel = value ?: "off"
    }

    companion object {
        const val REACT_CLASS = "KlarnaExpressCheckoutButton"
    }
}
