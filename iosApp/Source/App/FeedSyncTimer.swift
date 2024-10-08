//
//  FeedSyncTimer.swift
//  FeedFlow
//
//  Created by Marco Gomiero on 23/07/24.
//  Copyright © 2024 FeedFlow. All rights reserved.
//

import Foundation
import FeedFlowKit

class FeedSyncTimer {

    private var timer: Timer?

    func scheduleTimer() {
        timer?.invalidate()
        timer = Timer.scheduledTimer(withTimeInterval: 120, repeats: true) { _ in
            Deps.shared.getLogger(tag: "FeedSyncTimer").d(messageString: "Sync scheduled")
            Deps.shared.getFeedSyncRepository().enqueueBackup(forceBackup: false)
        }

    }

    func invalidate() {
        Deps.shared.getLogger(tag: "FeedSyncTimer").d(messageString: "Sync timer invalidated")
        timer?.invalidate()
    }
}
