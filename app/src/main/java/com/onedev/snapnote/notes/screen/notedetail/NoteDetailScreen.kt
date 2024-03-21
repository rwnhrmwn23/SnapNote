package com.onedev.snapnote.notes.screen.notedetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.onedev.snapnote.R
import com.onedev.snapnote.notes.data.Note
import com.onedev.snapnote.notes.utils.LoadingContent
import com.onedev.snapnote.notes.utils.NoteDetailsTopAppBar

@Composable
fun NoteDetailScreen(
    onEditNote: (String) -> Unit,
    onBack: () -> Unit,
    onDeleteNote: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NoteDetailViewModel = hiltViewModel()
) {
    val snackRarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackRarHostState) },
        modifier = modifier.fillMaxSize(),
        topBar = {
            NoteDetailsTopAppBar(
                onBack = { onBack() },
                onDelete = { viewModel.deleteNote() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onEditNote(viewModel.noteId) }) {
                Icon(Icons.Filled.Edit, stringResource(id = R.string.edit_note))
            }
        }
    ) { paddingValues ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        NoteDetailContent(
            loading = uiState.isLoading,
            empty = uiState.note == null && !uiState.isLoading,
            note = uiState.note,
            onNoteCheck = viewModel::setCompleted,
            onRefresh = viewModel::refresh,
            modifier = Modifier.padding(paddingValues)
        )

        uiState.userMessage?.let { userMessage ->
            val snackBarText = stringResource(id = userMessage)
            LaunchedEffect(snackRarHostState, viewModel, userMessage, snackBarText) {
                snackRarHostState.showSnackbar(snackBarText)
                viewModel.snackBarMessageShown()
            }
        }

        LaunchedEffect(uiState.isNoteDeleted) {
            if (uiState.isNoteDeleted) {
                onDeleteNote()
            }
        }
    }
}

@Composable
fun NoteDetailContent(
    loading: Boolean,
    empty: Boolean,
    note: Note?,
    onNoteCheck: (Boolean) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val screenPadding = Modifier.padding(
        horizontal = dimensionResource(id = R.dimen.horizontal_margin),
        vertical = dimensionResource(id = R.dimen.vertical_margin)
    )
    val commonModifier = modifier
        .fillMaxWidth()
        .then(screenPadding)

    LoadingContent(
        loading = loading,
        empty = empty,
        emptyContent = {
            Text(
                text = stringResource(id = R.string.no_data),
                modifier = commonModifier
            )
        },
        onRefresh = { onRefresh() },
        content = {
            Column(commonModifier.verticalScroll(rememberScrollState())) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .then(screenPadding)
                ) {
                    if (note != null) {
                        Checkbox(
                            checked = note.isCompleted,
                            onCheckedChange = onNoteCheck
                        )
                        Column {
                            Text(text = note.title, style = MaterialTheme.typography.headlineSmall)
                            Text(
                                text = note.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun NoteDetailContentPreview() {
    NoteDetailContent(
        loading = false,
        empty = false,
        note = Note(
            title = "Title",
            description = "Description",
            isCompleted = false,
            id = "ID"
        ),
        onNoteCheck = { },
        onRefresh = { }
    )
}

@Preview(showBackground = true)
@Composable
fun NoteDetailContentCompletedPreview() {
    NoteDetailContent(
        loading = false,
        empty = false,
        note = Note(
            title = "Title",
            description = "Description",
            isCompleted = true,
            id = "ID"
        ),
        onNoteCheck = { },
        onRefresh = { }
    )
}

@Preview(showBackground = true)
@Composable
fun NoteDetailContentEmptyPreview() {
    NoteDetailContent(
        loading = false,
        empty = true,
        note = Note(
            title = "Title",
            description = "Description",
            isCompleted = true,
            id = "ID"
        ),
        onNoteCheck = { },
        onRefresh = { }
    )
}