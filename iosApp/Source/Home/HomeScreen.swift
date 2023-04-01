//
//  HomeScreen.swift
//  FeedFlow
//
//  Created by Marco Gomiero on 27/03/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import KMPNativeCoroutinesAsync
import shared

struct HomeScreen: View {
    
    @EnvironmentObject var appState: AppState
    @StateObject var homeViewModel = KotlinDependencies.shared.getHomeViewModel()
    
    @State var loadingState: FeedUpdateStatus? = nil
    @State var feedState: [FeedItem] = []
    @State var errorState: UIErrorState? = nil
    
    @State private var showSettings = false
    
    var body: some View {
        HomeScreenContent(
            loadingState: $loadingState,
            feedState: $feedState,
            errorState: $errorState,
            onReloadClick: {
                // todo
            },
            onAddFeedClick: {
                // todo
            }
        )
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading) {
                Text("FeedFlow (\(feedState.count))")
                    .font(.title2)
            }
            
            ToolbarItem(placement: .primaryAction) {
                Button {
                    self.showSettings.toggle()
                } label: {
                    Image(systemName: "gear")
                }
            }
        }
        .sheet(isPresented: self.$showSettings) {
            SettingsScreen()
        }
        .task {
            do {
                let stream = asyncStream(for: homeViewModel.loadingStateNative)
                for try await state in stream {
                    self.loadingState = state
                }
            } catch {
                emitGenericError()
            }
        }
        .task {
            do {
                let stream = asyncStream(for: homeViewModel.errorStateNative)
                for try await state in stream {
                    self.errorState = state
                }
            } catch {
                emitGenericError()
            }
        }
        .task {
            do {
                let stream = asyncStream(for: homeViewModel.feedStateNative)
                for try await state in stream {
                    self.feedState = state
                }
            } catch {
                emitGenericError()
            }
        }
    }
    
    private func emitGenericError() {
        self.appState.snackbarData = SnackbarData(
            title: "Sorry, something went wrong :(",
            subtitle: nil,
            showBanner: true
        )
    }
}
