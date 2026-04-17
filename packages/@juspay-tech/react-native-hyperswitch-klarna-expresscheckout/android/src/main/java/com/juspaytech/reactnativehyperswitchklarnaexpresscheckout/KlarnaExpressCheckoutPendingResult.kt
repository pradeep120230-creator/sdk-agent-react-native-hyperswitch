package com.juspaytech.reactnativehyperswitchklarnaexpresscheckout

object KlarnaExpressCheckoutPendingResult {
    @Volatile
    private var finalizeCallback: FinalizeCallback? = null

    interface FinalizeCallback {
        fun onSuccess(
            approved: Boolean,
            showForm: Boolean,
            finalizeRequired: Boolean,
            authorizationToken: String?,
            clientToken: String?,
            sessionId: String?,
            collectedShippingAddress: String?,
            merchantReference1: String?,
            merchantReference2: String?
        )

        fun onError(name: String, message: String, isFatal: Boolean, sessionId: String?)
    }

    fun setFinalizeCallback(callback: FinalizeCallback?) {
        this.finalizeCallback = callback
    }

    fun notifyFinalizeSuccess(
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
        finalizeCallback?.onSuccess(
            approved,
            showForm,
            finalizeRequired,
            authorizationToken,
            clientToken,
            sessionId,
            collectedShippingAddress,
            merchantReference1,
            merchantReference2
        )
        finalizeCallback = null
    }

    fun notifyFinalizeError(name: String, message: String, isFatal: Boolean, sessionId: String?) {
        finalizeCallback?.onError(name, message, isFatal, sessionId)
        finalizeCallback = null
    }
}
