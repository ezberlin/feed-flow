//
//  BrowserSelector.swift
//  FeedFlow
//
//  Created by Marco Gomiero on 12/07/23.
//  Copyright © 2023 FeedFlow. All rights reserved.
//

import Foundation
import FeedFlowKit
import UIKit

@Observable class BrowserSelector {

    private let supportedBrowsers = [
        Browser(
            id: "",
            name: feedFlowStrings.defaultBrowser,
            isFavourite: false
        ),

        Browser(
            id: "googlechromes://",
            name: "Chrome",
            isFavourite: false
        ),

        Browser(
            id: "firefox://open-url?url=",
            name: "Firefox",
            isFavourite: false
        ),

        Browser(
            id: "firefox-focus://open-url?url=",
            name: "Firefox Focus",
            isFavourite: false
        ),

        Browser(
            id: "ddgQuickLink://",
            name: "DuckDuckGo",
            isFavourite: false
        ),

        Browser(
            id: "brave://open-url?url=",
            name: "Brave",
            isFavourite: false
        )
    ]

    private let browserSettingsRepository = Deps.shared.getBrowserSettingsRepository()
    private let settingsRepository = Deps.shared.getSettingsRepository()

    var browsers: [Browser] = []
    var selectedBrowser: Browser? {
        didSet {
            browserSettingsRepository.setFavouriteBrowser(browser: selectedBrowser!)
        }
    }

    init() {
        let favouriteBrowser = browserSettingsRepository.getFavouriteBrowserId()

        var isInAppBrowserFavourite = false
        if favouriteBrowser == nil {
            isInAppBrowserFavourite = true
        } else {
            isInAppBrowserFavourite = favouriteBrowser == BrowserIds.shared.IN_APP_BROWSER
        }

        let inAppBrowser =  Browser(
            id: BrowserIds.shared.IN_APP_BROWSER,
            name: feedFlowStrings.inAppBrowser,
            isFavourite: isInAppBrowserFavourite
        )
        browsers.append(inAppBrowser)
        if isInAppBrowserFavourite {
            selectedBrowser = inAppBrowser
        }

        supportedBrowsers.forEach { browser in
            let stringUrl = "\(browser.id)https://www.example.com"

            if let url = URL(string: stringUrl), UIApplication.shared.canOpenURL(url) {
                let updatedBrowser = Browser(
                    id: browser.id,
                    name: browser.name,
                    isFavourite: browser.id == favouriteBrowser
                )

                browsers.append(updatedBrowser)
                if updatedBrowser.isFavourite {
                    selectedBrowser = updatedBrowser
                }
            }
        }
    }

    func openInAppBrowser() -> Bool {
        return selectedBrowser?.id == BrowserIds.shared.IN_APP_BROWSER
    }

    func openReaderMode() -> Bool {
        return settingsRepository.isUseReaderModeEnabled()
    }

    func getUrlForDefaultBrowser(stringUrl: String) -> URL {
        let url: String
        if let selectedBrowser {
            if selectedBrowser.id == "googlechromes://" {
                url = "\(selectedBrowser.id)\(stringUrl.replacingOccurrences(of: "https://", with: ""))"
            } else {
                url = "\(selectedBrowser.id)\(stringUrl)"
            }
            return URL(string: url)!
        } else {
            return URL(string: stringUrl)!
        }
    }
}
