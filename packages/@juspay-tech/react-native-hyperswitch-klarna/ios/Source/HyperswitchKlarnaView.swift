import Foundation
import UIKit
import KlarnaMobileSDK

@objc(HyperswitchKlarnaView)
class HyperswitchKlarnaView: UIView, KlarnaPaymentEventListener {

    @objc var onInitialized: RCTDirectEventBlock?
    @objc var onLoaded: RCTDirectEventBlock?
    @objc var onAuthorized: RCTDirectEventBlock?
    @objc var onResize: RCTDirectEventBlock?

    @objc weak var bridge: RCTBridge?

    private var paymentView: KlarnaPaymentView?
    private var heightConstraint: NSLayoutConstraint?
    private var currentHeight: CGFloat = 0

    @objc dynamic var category: NSString = "klarna" {
        didSet { rebuildPaymentView() }
    }

    @objc dynamic var returnURL: NSString = "" {
        didSet { rebuildPaymentView() }
    }

    override init(frame: CGRect) {
        super.init(frame: frame)
        setupPaymentView()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupPaymentView()
    }

    private func setupPaymentView() {
        subviews.forEach { $0.removeFromSuperview() }
        guard let url = URL(string: returnURL as String), !(returnURL as String).isEmpty else {
            return
        }
        let view = KlarnaPaymentView(
            category: category as String,
            returnUrl: url,
            eventListener: self
        )
        view.translatesAutoresizingMaskIntoConstraints = false
        addSubview(view)
        let height = view.heightAnchor.constraint(equalToConstant: 0)
        heightConstraint = height
        NSLayoutConstraint.activate([
            view.topAnchor.constraint(equalTo: topAnchor),
            view.leadingAnchor.constraint(equalTo: leadingAnchor),
            view.trailingAnchor.constraint(equalTo: trailingAnchor),
            view.bottomAnchor.constraint(equalTo: bottomAnchor),
            height
        ])
        paymentView = view
    }

    private func rebuildPaymentView() {
        DispatchQueue.main.async { [weak self] in
            self?.setupPaymentView()
        }
    }

    @objc func initialize(clientToken: String, returnURL: String?) {
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }
            if let overrideReturn = returnURL, !overrideReturn.isEmpty, overrideReturn as NSString != self.returnURL {
                self.returnURL = overrideReturn as NSString
            }
            self.paymentView?.initialize(clientToken: clientToken)
        }
    }

    @objc func load() {
        DispatchQueue.main.async { [weak self] in
            self?.paymentView?.load()
        }
    }

    @objc func authorize() {
        DispatchQueue.main.async { [weak self] in
            self?.paymentView?.authorize()
        }
    }

    // MARK: - KlarnaPaymentEventListener

    func klarnaInitialized(paymentView: KlarnaPaymentView) {
        onInitialized?([:])
    }

    func klarnaLoaded(paymentView: KlarnaPaymentView) {
        onLoaded?([:])
    }

    func klarnaLoadedPaymentReview(paymentView: KlarnaPaymentView) {}

    func klarnaAuthorized(
        paymentView: KlarnaPaymentView,
        approved: Bool,
        authToken: String?,
        finalizeRequired: Bool
    ) {
        var payload: [String: Any] = [
            "authorized": true,
            "approved": approved,
            "finalizeRequired": finalizeRequired
        ]
        if let token = authToken {
            payload["authToken"] = token
        }
        onAuthorized?(payload)
    }

    func klarnaReauthorized(paymentView: KlarnaPaymentView, approved: Bool, authToken: String?) {
        var payload: [String: Any] = ["authorized": true, "approved": approved]
        if let token = authToken {
            payload["authToken"] = token
        }
        onAuthorized?(payload)
    }

    func klarnaFinalized(paymentView: KlarnaPaymentView, approved: Bool, authToken: String?) {
        var payload: [String: Any] = ["authorized": true, "approved": approved]
        if let token = authToken {
            payload["authToken"] = token
        }
        onAuthorized?(payload)
    }

    func klarnaResized(paymentView: KlarnaPaymentView, to newHeight: CGFloat) {
        currentHeight = newHeight
        heightConstraint?.constant = newHeight
        setNeedsLayout()
        let width = bounds.width
        bridge?.uiManager.setSize(CGSize(width: width, height: newHeight), for: self)
        onResize?(["width": width, "height": newHeight])
    }

    override var intrinsicContentSize: CGSize {
        return CGSize(width: UIView.noIntrinsicMetric, height: currentHeight)
    }

    func klarnaFailed(inPaymentView paymentView: KlarnaPaymentView, withError error: KlarnaPaymentError) {
        let payload: [String: Any] = [
            "authorized": false,
            "approved": false,
            "errorMessage": error.name
        ]
        onAuthorized?(payload)
    }
}
