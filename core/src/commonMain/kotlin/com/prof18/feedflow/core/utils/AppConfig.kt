package com.prof18.feedflow.core.utils

data class AppConfig(
    val appEnvironment: AppEnvironment,
    val isLoggingEnabled: Boolean,
    val isDropboxSyncEnabled: Boolean,
)
