package com.prof18.feedflow.feedsourcelist

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import com.prof18.feedflow.MR
import com.prof18.feedflow.addfeed.AddFeedScreen
import com.prof18.feedflow.core.model.CategoryId
import com.prof18.feedflow.core.model.FeedSource
import com.prof18.feedflow.core.model.FeedSourceListState
import com.prof18.feedflow.desktopViewModel
import com.prof18.feedflow.di.DI
import com.prof18.feedflow.presentation.FeedSourceListViewModel
import com.prof18.feedflow.presentation.preview.feedSourcesState
import com.prof18.feedflow.ui.components.FeedSourceLogoImage
import com.prof18.feedflow.ui.feedsourcelist.FeedSourceNavBar
import com.prof18.feedflow.ui.feedsourcelist.FeedSourcesWithCategoryList
import com.prof18.feedflow.ui.feedsourcelist.NoFeedSourcesView
import com.prof18.feedflow.ui.style.FeedFlowTheme
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun FeedSourceListScreen(
    navigateBack: () -> Unit,
) {
    FeedFlowTheme {
        var dialogState by remember { mutableStateOf(false) }

        DialogWindow(
            title = stringResource(MR.strings.add_feed),
            visible = dialogState,
            onCloseRequest = { dialogState = false },
        ) {
            AddFeedScreen(
                onFeedAdded = {
                    dialogState = false
                },
            )
        }
        val viewModel = desktopViewModel { DI.koin.get<FeedSourceListViewModel>() }

        val feedSources by viewModel.feedSourcesState.collectAsState()

        FeedSourceListContent(
            feedSourceListState = feedSources,
            onAddFeedClick = {
                dialogState = true
            },
            onDeleteFeedClick = { feedSource ->
                viewModel.deleteFeedSource(feedSource)
            },
            onExpandClicked = { categoryId ->
                viewModel.expandCategory(categoryId)
            },
            navigateBack = navigateBack,
        )
    }
}

@Composable
private fun FeedSourceListContent(
    feedSourceListState: FeedSourceListState,
    onAddFeedClick: () -> Unit,
    onDeleteFeedClick: (FeedSource) -> Unit,
    navigateBack: () -> Unit,
    onExpandClicked: (CategoryId?) -> Unit,
) {
    Scaffold(
        topBar = {
            FeedSourceNavBar(
                navigateBack = navigateBack,
                onAddFeedSourceClick = onAddFeedClick,
            )
        },
    ) { paddingValues ->
        if (feedSourceListState.isEmpty()) {
            NoFeedSourcesView(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
            )
        } else {
            FeedSourcesWithCategoryList(
                modifier = Modifier
                    .padding(paddingValues),
                feedSourceState = feedSourceListState,
                onExpandClicked = onExpandClicked,
                feedSourceImage = { imageUrl ->
                    FeedSourceLogoImage(
                        size = 24.dp,
                        imageUrl = imageUrl,
                    )
                },
                onDeleteFeedSourceClick = onDeleteFeedClick,
            )
        }
    }
}

@Preview
@Composable
private fun FeedSourceListContentPreview() {
    FeedFlowTheme {
        FeedSourceListContent(
            feedSourceListState = FeedSourceListState(
                feedSourcesWithoutCategory = emptyList(),
                feedSourcesWithCategory = feedSourcesState,
            ),
            onAddFeedClick = {},
            onDeleteFeedClick = {},
            onExpandClicked = {},
            navigateBack = {},
        )
    }
}

@Preview
@Composable
private fun FeedSourceListContentDarkPreview() {
    FeedFlowTheme(
        darkTheme = true,
    ) {
        FeedSourceListContent(
            feedSourceListState = FeedSourceListState(
                feedSourcesWithoutCategory = emptyList(),
                feedSourcesWithCategory = feedSourcesState,
            ),
            onAddFeedClick = {},
            onDeleteFeedClick = {},
            onExpandClicked = {},
            navigateBack = {},
        )
    }
}
