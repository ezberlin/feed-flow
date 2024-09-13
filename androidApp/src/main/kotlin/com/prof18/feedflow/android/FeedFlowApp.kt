package com.prof18.feedflow.android

import android.app.Application
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.prof18.feedflow.android.readermode.ReaderModeViewModel
import com.prof18.feedflow.core.utils.AppConfig
import com.prof18.feedflow.core.utils.AppEnvironment
import com.prof18.feedflow.shared.di.getWith
import com.prof18.feedflow.shared.di.initKoin
import com.prof18.feedflow.shared.domain.feedsync.FeedSyncRepository
import com.prof18.feedflow.shared.ui.utils.coilImageLoader
import org.koin.android.ext.android.inject
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class FeedFlowApp : Application() {

    private val feedSyncRepo by inject<FeedSyncRepository>()

    override fun onCreate() {
        super.onCreate()

        val isGooglePlayFlavor = when (BuildConfig.FLAVOR) {
            "googlePlay" -> true
            else -> false
        }
        val appEnvironment = if (BuildConfig.DEBUG) {
            AppEnvironment.Debug
        } else {
            AppEnvironment.Release
        }
        val appConfig = AppConfig(
            appEnvironment = appEnvironment,
            isLoggingEnabled = isGooglePlayFlavor,
            isDropboxSyncEnabled = isGooglePlayFlavor,
        )

        val crashlyticsHelper = CrashlyticsHelper()
        if (isGooglePlayFlavor && appEnvironment.isRelease()) {
            crashlyticsHelper.initCrashlytics()
        }

        initKoin(
            appConfig = appConfig,
            platformSetup = {
                workManagerFactory()
            },
            crashReportingLogWriter = crashlyticsHelper.crashReportingLogWriter(),
            modules = listOf(
                module {
                    single<Context> { this@FeedFlowApp }
                    single {
                        BrowserManager(
                            context = this@FeedFlowApp,
                            browserSettingsRepository = get(),
                            logger = getWith("BrowserManager"),
                            settingsRepository = get(),
                        )
                    }
                    single {
                        coilImageLoader(
                            context = this@FeedFlowApp,
                            debug = appEnvironment.isDebug(),
                        )
                    }
                    single { appConfig }
                    viewModel {
                        ReaderModeViewModel(
                            readerModeExtractor = get(),
                            settingsRepository = get(),
                        )
                    }
                },
            ),
        )

        with(ProcessLifecycleOwner.get()) {
            lifecycle.addObserver(
                object : DefaultLifecycleObserver {
                    override fun onStop(owner: LifecycleOwner) {
                        super.onStop(owner)
                        feedSyncRepo.enqueueBackup()
                    }
                },
            )
        }
    }
}
