package com.onedev.snapnote.notes.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

val primaryDarkColor: Color = Color(0xFF263238)

@Composable
fun LoadingContent(
    loading: Boolean,
    empty: Boolean,
    emptyContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (empty) {
        emptyContent()
    }
    else {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = loading),
            onRefresh = onRefresh,
            modifier = modifier,
            content = content,
        )
    }
}