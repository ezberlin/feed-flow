package com.prof18.feedflow.domain.opml

import com.prof18.feedflow.domain.model.ParsedFeedSource

internal expect class OPMLFeedParser {
    suspend fun parse(feed: String): List<ParsedFeedSource>
}