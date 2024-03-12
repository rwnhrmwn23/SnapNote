package com.onedev.snapnote.notes.screen.addeditnote

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.onedev.snapnote.R
import com.onedev.snapnote.notes.utils.AddEditNoteAppBar

@Composable
fun AddEditNoteScreen(
    @StringRes topAppBarTitle: Int,
    onNoteUpdate: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEditNoteViewModel = hiltViewModel()
) {
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            AddEditNoteAppBar(title = topAppBarTitle, onBack)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.saveNote() }) {
                Icon(Icons.Filled.Done, stringResource(id = R.string.cd_save_note))
            }
        }
    ) { paddingValues ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        AddEditNoteContent(
            loading = uiState.isLoading,
            title = uiState.title,
            description = uiState.description,
            onTitleChanged = viewModel::updateTitle,
            onDescriptionChanged = viewModel::updateDescription,
            modifier = Modifier.padding(paddingValues)
        )

        LaunchedEffect(uiState.isNoteSaved) {
            if (uiState.isNoteSaved) {
                onNoteUpdate()
            }
        }

        uiState.userMessage?.let {userMessage ->
            val snackBarText = stringResource(userMessage)
            LaunchedEffect(snackBarHostState, viewModel, userMessage, snackBarText) {
                snackBarHostState.showSnackbar(snackBarText)
                viewModel.snackBarMessageShown()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteContent(
    loading: Boolean,
    title: String,
    description: String,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (loading) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = true),
            onRefresh = { },
            content = { },
        )
    } else {
        Column(
            modifier
                .fillMaxSize()
                .padding(all = dimensionResource(id = R.dimen.horizontal_margin))
                .verticalScroll(rememberScrollState())
        ) {
            val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6F)
            )
            OutlinedTextField(
                value = title,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = onTitleChanged,
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.title_hint),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                colors = textFieldColors
            )
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChanged,
                placeholder = {
                    Text(stringResource(id = R.string.description_hint))
                },
                modifier = Modifier
                    .height(350.dp)
                    .fillMaxWidth(),
                colors = textFieldColors
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddEditNoteContentPreview() {
    AddEditNoteContent(
        loading = false,
        title = "Title",
        description = "Description",
        onTitleChanged = {},
        onDescriptionChanged = {},
        modifier = Modifier.padding(8.dp)
    )
}