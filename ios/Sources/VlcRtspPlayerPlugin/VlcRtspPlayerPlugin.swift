import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(VlcRtspPlayerPlugin)
public class VlcRtspPlayerPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "VlcRtspPlayerPlugin"
    public let jsName = "VlcRtspPlayer"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "echo", returnType: CAPPluginReturnPromise)
    ]
    private let implementation = VlcRtspPlayer()

    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.resolve([
            "value": implementation.echo(value)
        ])
    }
}
