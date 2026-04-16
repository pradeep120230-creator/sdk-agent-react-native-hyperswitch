import Foundation
import React

@objc(HyperswitchKlarnaPaymentView)
class HyperswitchKlarnaViewManager: RCTViewManager {

    override func view() -> UIView {
        let view = HyperswitchKlarnaView()
        view.bridge = bridge
        return view
    }

    @objc override static func requiresMainQueueSetup() -> Bool {
        return true
    }

    @objc func initialize(_ node: NSNumber, clientToken: String, returnURL: NSString?) {
        DispatchQueue.main.async { [weak self] in
            guard
                let bridge = self?.bridge,
                let view = bridge.uiManager.view(forReactTag: node) as? HyperswitchKlarnaView
            else { return }
            view.initialize(clientToken: clientToken, returnURL: returnURL as String?)
        }
    }

    @objc func load(_ node: NSNumber) {
        DispatchQueue.main.async { [weak self] in
            guard
                let bridge = self?.bridge,
                let view = bridge.uiManager.view(forReactTag: node) as? HyperswitchKlarnaView
            else { return }
            view.load()
        }
    }

    @objc func authorize(_ node: NSNumber) {
        DispatchQueue.main.async { [weak self] in
            guard
                let bridge = self?.bridge,
                let view = bridge.uiManager.view(forReactTag: node) as? HyperswitchKlarnaView
            else { return }
            view.authorize()
        }
    }
}
