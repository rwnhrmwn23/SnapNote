package com.onedev.snapnote.notes.utils

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.onedev.snapnote.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesTopAppBar(
    openDrawer: () -> Unit,
    onFilterAllNotes: () -> Unit,
    onFilterActiveNotes: () -> Unit,
    onFilterCompletedNotes: () -> Unit,
    onClearCompletedNoted: () -> Unit,
    onRefresh: () -> Unit,
) {
    TopAppBar(
        title = { Text(stringResource(id = R.string.app_name)) },
        navigationIcon = {
            IconButton(onClick = { openDrawer() }) {
                Icon(Icons.Filled.Menu, stringResource(id = R.string.open_drawer))
            }
        },
        actions = {
            FilterNotesMenu(onFilterAllNotes, onFilterActiveNotes, onFilterCompletedNotes)
            MoreNotesMenu(onClearCompletedNoted, onRefresh)
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun FilterNotesMenu(
    onFilterAllNotes: () -> Unit,
    onFilterActiveNotes: () -> Unit,
    onFilterCompletedNotes: () -> Unit,
) {
    TopAppBarDropDownMenu(
        iconContent = {
            Icon(
                painterResource(id = R.drawable.ic_filter_list),
                stringResource(id = R.string.menu_filter)
            )
        }
    ) { closeMenu ->
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.nav_all)) },
            onClick = { onFilterAllNotes(); closeMenu() }
        )
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.nav_active)) },
            onClick = { onFilterActiveNotes(); closeMenu() }
        )
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.nav_completed)) },
            onClick = { onFilterCompletedNotes(); closeMenu() }
        )
    }
}

@Composable
fun MoreNotesMenu(
    onClearCompletedNotes: () -> Unit,
    onRefresh: () -> Unit,
) {
    TopAppBarDropDownMenu(
        iconContent = {
            Icon(Icons.Filled.MoreVert, stringResource(id = R.string.menu_more))
        }
    ) { closeMenu ->
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.menu_clear)) },
            onClick = { onClearCompletedNotes(); closeMenu() }
        )
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.refresh)) },
            onClick = { onRefresh(); closeMenu() }
        )
    }
}

@Composable
fun TopAppBarDropDownMenu(
    iconContent: @Composable () -> Unit,
    content: @Composable ColumnScope.(() -> Unit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        IconButton(onClick = { expanded = !expanded }) {
            iconContent()
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.wrapContentSize(Alignment.TopEnd)
        ) {
            content { expanded = !expanded }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticTopAppBar(openDrawer: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(id = R.string.statistics_title)) },
        navigationIcon = {
            IconButton(onClick = { openDrawer() }) {
                Icon(Icons.Filled.Menu, stringResource(id = R.string.open_drawer))
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailsTopAppBar(onBack: () -> Unit, onDelete: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(id = R.string.note_details)) },
        navigationIcon = {
            IconButton(onClick = { onBack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(id = R.string.menu_back))
            }
        },
        actions = {
            IconButton(onClick = { onDelete() }) {
                Icon(Icons.Filled.Delete, stringResource(id = R.string.menu_delete_note))
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteAppBar(@StringRes title: Int, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(id = title)) },
        navigationIcon = {
            IconButton(onClick = { onBack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(id = R.string.menu_back))
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview
@Composable
fun NotesTopAppBarPreview() {
    NotesTopAppBar({}, {}, {}, {}, {}, {})
}

@Preview
@Composable
fun StatisticTopAppBarPreview() {
    StatisticTopAppBar {}
}

@Preview
@Composable
fun NoteDetailsTopAppBarPreview() {
    NoteDetailsTopAppBar({}, {})
}

@Preview
@Composable
fun AddEditNoteAppBarPreview() {
    AddEditNoteAppBar(R.string.add_note) {}
}