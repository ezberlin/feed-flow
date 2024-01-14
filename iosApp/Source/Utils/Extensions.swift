//
//  Extensions.swift
//  FeedFlow
//
//  Created by Marco Gomiero on 15/07/23.
//  Copyright © 2023 FeedFlow. All rights reserved.
//

import shared
import SwiftUI

extension StringResource {
    var localized: String {
        return self.desc().localizedWithFallback()
   }
}

extension View {
    /// Applies the given transform if the given condition evaluates to `true`.
    /// - Parameters:
    ///   - condition: The condition to evaluate.
    ///   - transform: The transform to apply to the source `View`.
    /// - Returns: Either the original `View` or the modified `View` if the condition is `true`.
    @ViewBuilder func `if`<Content: View>(_ condition: Bool, transform: (Self) -> Content) -> some View {
        if condition {
            transform(self)
        } else {
            self
        }
    }

    func modify<Content>(@ViewBuilder _ transform: (Self) -> Content) -> Content {
            transform(self)
    }
}
