import Foundation
import React

@objc(KlarnaExpressCheckoutButton)
class KlarnaExpressCheckoutButtonViewManager: RCTViewManager {

  override func view() -> UIView! {
    return KlarnaExpressCheckoutButtonView()
  }

  @objc override static func requiresMainQueueSetup() -> Bool {
    return true
  }
}
