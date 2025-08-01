// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "VlcRtspPlayer",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "VlcRtspPlayer",
            targets: ["VlcRtspPlayerPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "VlcRtspPlayerPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/VlcRtspPlayerPlugin"),
        .testTarget(
            name: "VlcRtspPlayerPluginTests",
            dependencies: ["VlcRtspPlayerPlugin"],
            path: "ios/Tests/VlcRtspPlayerPluginTests")
    ]
)