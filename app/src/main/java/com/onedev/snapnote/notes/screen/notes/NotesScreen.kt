package com.onedev.snapnote.notes.screen.notes

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.onedev.snapnote.R
import com.onedev.snapnote.notes.data.Note
import com.onedev.snapnote.notes.utils.LoadingContent
import com.onedev.snapnote.notes.utils.NotesTopAppBar

@Composable
fun NotesScreen(
    @StringRes userMessage: Int,
    onAddNote: () -> Unit,
    onNoteClick: (Note) -> Unit,
    onUserMessageDisplayed: () -> Unit,
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotesViewModel = hiltViewModel(),
) {
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            NotesTopAppBar(
                openDrawer = openDrawer,
                onFilterAllNotes = { viewModel.setFiltering(NotesFilterType.ALL_NOTES) },
                onFilterActiveNotes = { viewModel.setFiltering(NotesFilterType.ACTIVE_NOTES) },
                onFilterCompletedNotes = { viewModel.setFiltering(NotesFilterType.COMPLETED_NOTES) },
                onClearCompletedNoted = { viewModel.clearCompletedNotes() },
                onRefresh = { viewModel.refresh() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNote) {
                Icon(Icons.Filled.Add, stringResource(id = R.string.add_note))
            }
        }
    ) { paddingValues ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        NotesContent(
            loading = uiState.isLoading,
            notes = uiState.items,
            currentFilteringLabel = uiState.filteringUiInfo.currentFilteringLabel,
            noNotesLabel = uiState.filteringUiInfo.noNotesLabel,
            noNotesIconRes = uiState.filteringUiInfo.noNotesIconRes,
            onRefresh = viewModel::refresh,
            onNotesClick = onNoteClick,
            onNotesCheckedChange = viewModel::completeNote,
            modifier = Modifier.padding(paddingValues)
        )

        // Check for user messages to display on the screen
        uiState.userMessage?.let { message ->
            val snackBarText = stringResource(message)
            LaunchedEffect(snackBarHostState, viewModel, message, snackBarText) {
                snackBarHostState.showSnackbar(snackBarText)
                viewModel.snackBarMessageShown()
            }
        }

        // Check if there's a userMessage to show to the user
        val currentOnUserMessageDisplayed by rememberUpdatedState(onUserMessageDisplayed)
        LaunchedEffect(userMessage) {
            if (userMessage != 0) {
                viewModel.showEditResultMessage(userMessage)
                currentOnUserMessageDisplayed()
            }
        }
    }
}

@Composable
fun NotesContent(
    loading: Boolean,
    notes: List<Note>,
    @StringRes currentFilteringLabel: Int,
    @StringRes noNotesLabel: Int,
    @DrawableRes noNotesIconRes: Int,
    onRefresh: () -> Unit,
    onNotesClick: (Note) -> Unit,
    onNotesCheckedChange: (Note, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    LoadingContent(
        loading = loading,
        onRefresh = onRefresh,
        empty = notes.isEmpty() && !loading,
        emptyContent = {
            NotesEmptyContent(
                noNotesLabel = noNotesLabel,
                noNotesIconRes = noNotesIconRes,
                modifier = modifier
            )
        },
        content = {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensionResource(id = R.dimen.horizontal_margin))
            ) {
                Text(
                    text = stringResource(currentFilteringLabel),
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(id = R.dimen.list_item_padding),
                        vertical = dimensionResource(id = R.dimen.vertical_margin)
                    ),
                    style = MaterialTheme.typography.titleLarge
                )
                LazyColumn {
                    items(notes) { note ->
                        NoteItem(
                            note = note,
                            onNotesClick = onNotesClick,
                            onCheckedChange = {
                                onNotesCheckedChange(note, it)
                            },
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun NoteItem(
    note: Note,
    onCheckedChange: (Boolean) -> Unit,
    onNotesClick: (Note) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(id = R.dimen.horizontal_margin),
                vertical = dimensionResource(id = R.dimen.list_item_padding)
            )
            .clickable { onNotesClick(note) }
    ) {
        Checkbox(
            checked = note.isCompleted,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = note.title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(
                start = dimensionResource(id = R.dimen.horizontal_margin)
            ),
            textDecoration = if (note.isCompleted) {
                TextDecoration.LineThrough
            } else {
                null
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NoteItemPreview() {
    Column {
        NoteItem(
            note = Note(
                id = "1",
                title = "Note 1",
                description = "Description 1",
                isCompleted = false
            ),
            onNotesClick = { },
            onCheckedChange = { }
        )
        NoteItem(
            note = Note(
                id = "2",
                title = "Note 2",
                description = "Description 2",
                isCompleted = true
            ),
            onNotesClick = { },
            onCheckedChange = { }
        )
    }
}

@Composable
fun NotesEmptyContent(
    @StringRes noNotesLabel: Int,
    @DrawableRes noNotesIconRes: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = noNotesIconRes),
            contentDescription = stringResource(id = R.string.no_notes_image_content_description),
            modifier = Modifier.size(96.dp)
        )
        Text(text = stringResource(id = noNotesLabel))
    }
}

@Preview
@Composable
fun NotesContentPreview() {
    Surface {
        NotesContent(
            loading = false,
            notes = listOf(
                Note(
                    id = "1",
                    title = "Note 1",
                    description = "Description 1",
                    isCompleted = false
                ),
                Note(
                    id = "2",
                    title = "Note 2",
                    description = "Description 2",
                    isCompleted = true
                ),
                Note(
                    id = "3",
                    title = "Note 3",
                    description = "Description 3",
                    isCompleted = false
                )
            ),
            currentFilteringLabel = R.string.label_all,
            noNotesLabel = R.string.no_notes_all,
            noNotesIconRes = R.drawable.logo_no_fill,
            onRefresh = { },
            onNotesClick = { },
            onNotesCheckedChange = { _, _ -> }
        )
    }
}

@Preview
@Composable
fun NotesEmptyContentPreview() {
    Surface {
        NotesContent(
            loading = false,
            notes = emptyList(),
            currentFilteringLabel = R.string.label_all,
            noNotesLabel = R.string.no_notes_all,
            noNotesIconRes = R.drawable.logo_no_fill,
            onRefresh = { },
            onNotesClick = { },
            onNotesCheckedChange = { _, _ -> }
        )
    }
}