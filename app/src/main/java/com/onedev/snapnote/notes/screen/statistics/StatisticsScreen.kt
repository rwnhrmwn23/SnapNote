package com.onedev.snapnote.notes.screen.statistics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.onedev.snapnote.R
import com.onedev.snapnote.notes.utils.LoadingContent
import com.onedev.snapnote.notes.utils.StatisticTopAppBar

@Composable
fun StatisticScreen(
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    Scaffold(topBar = {
        StatisticTopAppBar(openDrawer)
    }) { paddingValues ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        StatisticsContent(
            loading = uiState.isLoading,
            empty = uiState.isEmpty,
            activeNotesPercent = uiState.activeNotesPercent,
            completedNotesPercent = uiState.completedNotesPercent,
            onRefresh = { viewModel.refresh() },
            modifier = modifier.padding(paddingValues)
        )
    }
}

@Composable
fun StatisticsContent(
    loading: Boolean,
    empty: Boolean,
    activeNotesPercent: Float,
    completedNotesPercent: Float,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val commonModifier = modifier
        .fillMaxSize()
        .padding(all = dimensionResource(id = R.dimen.horizontal_margin))

    LoadingContent(loading = loading, empty = empty, emptyContent = {
        Text(
            text = stringResource(id = R.string.statistics_no_notes), modifier = commonModifier
        )
    }, content = {
        Column(
            commonModifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (!loading) {
                Text(stringResource(id = R.string.statistics_active_notes, activeNotesPercent))
                Text(
                    stringResource(
                        id = R.string.statistics_completed_notes, completedNotesPercent
                    )
                )
            }
        }
    }, onRefresh = { onRefresh() })
}

@Preview
@Composable
fun StatisticsContentPreview() {
    StatisticsContent(loading = false,
        empty = false,
        activeNotesPercent = 80f,
        completedNotesPercent = 20f,
        onRefresh = { })
}

@Preview
@Composable
fun StatisticsContentEmptyPreview() {
    StatisticsContent(loading = false,
        empty = true,
        activeNotesPercent = 0f,
        completedNotesPercent = 0f,
        onRefresh = { })
}