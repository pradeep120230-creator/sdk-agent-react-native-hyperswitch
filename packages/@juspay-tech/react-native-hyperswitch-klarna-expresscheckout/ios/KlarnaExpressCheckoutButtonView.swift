import Foundation
import UIKit
import React
import KlarnaMobileSDK

@objc(KlarnaExpressCheckoutButtonView)
class KlarnaExpressCheckoutButtonView: UIView {

  @objc var onAuthorized: RCTDirectEventBlock?
  @objc var onError: RCTDirectEventBlock?

  @objc dynamic var clientToken: NSString = "" {
    didSet { rebuildButton() }
  }
  @objc dynamic var sessionData: NSString = "" {
    didSet { rebuildButton() }
  }
  @objc dynamic var autoFinalize: Bool = true {
    didSet { rebuildButton() }
  }
  @objc dynamic var collectShippingAddress: Bool = false {
    didSet { rebuildButton() }
  }
  @objc dynamic var locale: NSString = "en-US" {
    didSet { rebuildButton() }
  }
  @objc dynamic var environment: NSString = "production" {
    didSet { rebuildButton() }
  }
  @objc dynamic var region: NSString = "NA" {
    didSet { rebuildButton() }
  }
  @objc dynamic var theme: NSString = "dark" {
    didSet { rebuildButton() }
  }
  @objc dynamic var buttonShape: NSString = "rounded_rect" {
    didSet { rebuildButton() }
  }
  @objc dynamic var buttonStyleValue: NSString = "filled" {
    didSet { rebuildButton() }
  }
  @objc dynamic var returnUrl: NSString = "" {
    didSet { rebuildButton() }
  }
  @objc dynamic var loggingLevel: NSString = "off" {
    didSet { rebuildButton() }
  }

  private var klarnaButton: KlarnaExpressCheckoutButton?

  override init(frame: CGRect) {
    super.init(frame: frame)
  }

  required init?(coder: NSCoder) {
    super.init(coder: coder)
  }

  override func didMoveToWindow() {
    super.didMoveToWindow()
    if window != nil {
      rebuildButton()
    }
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    klarnaButton?.frame = bounds
  }

  private func rebuildButton() {
    DispatchQueue.main.async { [weak self] in
      guard let self = self else { return }

      self.klarnaButton?.removeFromSuperview()
      self.klarnaButton = nil

      let token = self.clientToken as String
      guard !token.isEmpty else { return }

      let sessionOptions = KlarnaExpressCheckoutSessionOptions.ServerSideSession(
        clientToken: token,
        sessionData: (self.sessionData as String).isEmpty ? nil : (self.sessionData as String),
        autoFinalize: self.autoFinalize,
        collectShippingAddress: self.collectShippingAddress
      )

      let styleConfiguration = KlarnaExpressCheckoutButtonStyleConfiguration(
        theme: self.mapButtonTheme(self.theme as String),
        shape: self.mapButtonShape(self.buttonShape as String),
        style: self.mapButtonStyle(self.buttonStyleValue as String)
      )

      let returnUrlString = self.returnUrl as String
      let effectiveReturnUrl = returnUrlString.isEmpty ? "hyperswitchsdk://klarna" : returnUrlString

      let options = KlarnaExpressCheckoutButtonOptions(
        sessionOptions: sessionOptions,
        returnUrl: effectiveReturnUrl,
        delegate: self,
        locale: self.locale as String,
        styleConfiguration: styleConfiguration,
        environment: self.mapEnvironment(self.environment as String),
        region: self.mapRegion(self.region as String),
        theme: self.mapFlowTheme(self.theme as String),
        loggingLevel: self.mapLoggingLevel(self.loggingLevel as String)
      )

      let button = KlarnaExpressCheckoutButton(options: options)
      button.frame = self.bounds
      button.autoresizingMask = [.flexibleWidth, .flexibleHeight]
      self.addSubview(button)
      self.klarnaButton = button
    }
  }

  private func mapButtonTheme(_ value: String) -> KlarnaButtonTheme {
    switch value.lowercased() {
    case "light": return .light
    case "auto": return .auto
    default: return .dark
    }
  }

  private func mapButtonShape(_ value: String) -> KlarnaButtonShape {
    switch value.lowercased() {
    case "rectangle": return .rectangle
    case "pill": return .pill
    default: return .rounded_rect
    }
  }

  private func mapButtonStyle(_ value: String) -> KlarnaButtonStyle {
    switch value.lowercased() {
    case "outlined": return .outlined
    default: return .filled
    }
  }

  private func mapFlowTheme(_ value: String) -> KlarnaTheme {
    switch value.lowercased() {
    case "dark": return .dark
    case "auto": return .automatic
    default: return .light
    }
  }

  private func mapEnvironment(_ value: String) -> KlarnaEnvironment {
    switch value.lowercased() {
    case "playground": return .playground
    default: return .production
    }
  }

  private func mapRegion(_ value: String) -> KlarnaRegion {
    switch value.uppercased() {
    case "EU": return .eu
    case "OC": return .oc
    default: return .na
    }
  }

  private func mapLoggingLevel(_ value: String) -> KlarnaLoggingLevel {
    switch value.lowercased() {
    case "error": return .error
    case "verbose": return .verbose
    default: return .off
    }
  }
}

extension KlarnaExpressCheckoutButtonView: KlarnaExpressCheckoutButtonDelegate {
  func onAuthorized(
    view: KlarnaExpressCheckoutButton,
    response: KlarnaExpressCheckoutButtonAuthorizationResponse
  ) {
    var payload: [String: Any] = [
      "approved": response.approved,
      "showForm": response.showForm,
      "finalizeRequired": response.finalizeRequired,
    ]
    if let token = response.authorizationToken { payload["authorizationToken"] = token }
    if let clientToken = response.clientToken { payload["clientToken"] = clientToken }
    payload["sessionId"] = response.sessionId
    if let shipping = response.collectedShippingAddress {
      payload["collectedShippingAddress"] = shipping
    }
    if let ref1 = response.merchantReference1 { payload["merchantReference1"] = ref1 }
    if let ref2 = response.merchantReference2 { payload["merchantReference2"] = ref2 }

    onAuthorized?(payload)
  }

  func onError(view: KlarnaExpressCheckoutButton, error: KlarnaError) {
    var payload: [String: Any] = [
      "name": error.name,
      "message": error.message,
      "isFatal": error.isFatal,
    ]
    if let sid = error.sessionId { payload["sessionId"] = sid }
    onError?(payload)
  }
}
