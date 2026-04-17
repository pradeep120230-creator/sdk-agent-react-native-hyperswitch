import Foundation
import React
import KlarnaMobileSDK

@objc(HyperswitchKlarnaExpressCheckout)
class HyperswitchKlarnaExpressCheckout: NSObject {

  @objc static func requiresMainQueueSetup() -> Bool {
    return true
  }

  private var finalizeCallback: RCTResponseSenderBlock?
  private var finalizeView: KlarnaPaymentView?

  @objc(finalize:callback:)
  func finalize(_ clientToken: String, callback: @escaping RCTResponseSenderBlock) {
    guard !clientToken.isEmpty else {
      callback([
        [
          "approved": false,
          "showForm": false,
          "finalizeRequired": false,
          "errorName": "InvalidClientToken",
          "errorMessage": "Missing clientToken for finalize",
          "isFatal": true,
        ]
      ])
      return
    }

    self.finalizeCallback = callback

    DispatchQueue.main.async { [weak self] in
      guard let self = self else { return }
      let view = KlarnaPaymentView(category: "pay_later", eventListener: self)
      self.finalizeView = view
      view.initialize(clientToken: clientToken, returnUrl: URL(string: "hyperswitchsdk://klarna") ?? URL(fileURLWithPath: "/"))
    }
  }

  private func invokeSuccess(approved: Bool, authToken: String?) {
    guard let callback = finalizeCallback else { return }
    finalizeCallback = nil
    finalizeView = nil
    var payload: [String: Any] = [
      "approved": approved,
      "showForm": false,
      "finalizeRequired": false,
    ]
    if let t = authToken { payload["authorizationToken"] = t }
    callback([payload])
  }

  private func invokeError(name: String, message: String, isFatal: Bool) {
    guard let callback = finalizeCallback else { return }
    finalizeCallback = nil
    finalizeView = nil
    callback([
      [
        "approved": false,
        "showForm": false,
        "finalizeRequired": false,
        "errorName": name,
        "errorMessage": message,
        "isFatal": isFatal,
      ]
    ])
  }
}

extension HyperswitchKlarnaExpressCheckout: KlarnaPaymentEventListener {
  func klarnaInitialized(paymentView: KlarnaPaymentView) {
    DispatchQueue.main.async {
      paymentView.load(jsonData: nil)
    }
  }

  func klarnaLoaded(paymentView: KlarnaPaymentView) {
    DispatchQueue.main.async {
      paymentView.finalise(jsonData: nil)
    }
  }

  func klarnaLoadedPaymentReview(paymentView: KlarnaPaymentView) {}

  func klarnaAuthorized(
    paymentView: KlarnaPaymentView,
    approved: Bool,
    authToken: String?,
    finalizeRequired: Bool
  ) {}

  func klarnaReauthorized(paymentView: KlarnaPaymentView, approved: Bool, authToken: String?) {}

  func klarnaFinalized(paymentView: KlarnaPaymentView, approved: Bool, authToken: String?) {
    invokeSuccess(approved: approved, authToken: authToken)
  }

  func klarnaFailed(inPaymentView paymentView: KlarnaPaymentView, withError error: KlarnaPaymentError) {
    invokeError(name: error.name, message: error.message, isFatal: error.isFatal)
  }
}
