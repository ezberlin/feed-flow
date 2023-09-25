package com.prof18.feedflow.core.model

data class FeedSource(
    val id: Int,
    val url: String,
    val title: String,
    val categoryName: CategoryName?,
    val lastSyncTimestamp: Long?,
)
