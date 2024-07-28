//
//  AccountScreenContent.swift
//  FeedFlow
//
//  Created by Marco Gomiero on 28/06/24.
//  Copyright © 2024 FeedFlow. All rights reserved.
//

import Foundation
import SwiftUI
import shared

struct AccountsScreenContent: View {
    @State private var showAddAccountSheet = false

    let syncAccount: SyncAccounts
    let supportedAccounts: [SyncAccounts]

    var body: some View {
        Form {
            Section {
                Text(feedFlowStrings.accountsDescription)
            }

            switch syncAccount {
            case SyncAccounts.dropbox:
                NavigationLink(destination: DropboxSyncScreen()) {
                    AccountsItem(
                        title: "Dropbox",
                        icon: "shippingbox",
                        showCheckmark: true
                    )
                }

            case SyncAccounts.icloud:
                NavigationLink(destination: ICloudSyncScreen()) {
                    AccountsItem(
                        title: "iCloud",
                        icon: "icloud",
                        showCheckmark: true
                    )
                }

            default:
                EmptyView()
            }

            Button {
                self.showAddAccountSheet.toggle()
            } label: {
                Label(feedFlowStrings.addAccountButton, systemImage: "plus.app")
            }.disabled(syncAccount != SyncAccounts.local)
        }
        .sheet(isPresented: $showAddAccountSheet) {
            AddAccountScreen(
                supportedAccounts: supportedAccounts
            )
        }
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .principal) {
                HStack {
                    Text(feedFlowStrings.settingsAccounts)
                        .font(.headline)

                    Text("BETA")
                        .font(.footnote)
                        .foregroundColor(.gray)
                }
            }
        }
    }
}

private struct AccountsItem: View {
    let title: String
    let icon: String
    let showCheckmark: Bool

    var body: some View {
        HStack {
            Image(systemName: icon)
            Text(title)
                .font(.body)
            Spacer()
            if showCheckmark {
                Image(systemName: "checkmark")
            }
        }
    }
}

#Preview {
    AccountsScreenContent(
        syncAccount: SyncAccounts.local,
        supportedAccounts: [
            SyncAccounts.dropbox,
            SyncAccounts.icloud
        ]
    )
}
