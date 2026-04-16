package com.reactnativehyperswitchklarna

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule

class HyperswitchKlarnaModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String = NAME

    companion object {
        const val NAME = "HyperswitchKlarna"
    }
}
