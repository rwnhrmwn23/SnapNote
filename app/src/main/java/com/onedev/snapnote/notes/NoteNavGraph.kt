package com.onedev.snapnote.notes

import android.app.Activity
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import androidx.navigation.navArgument
import com.onedev.snapnote.R
import com.onedev.snapnote.notes.NoteDestinationArgs.NOTE_ID_ARG
import com.onedev.snapnote.notes.NoteDestinationArgs.TITLE_ARG
import com.onedev.snapnote.notes.NoteDestinationArgs.USER_MESSAGE_ARGS
import com.onedev.snapnote.notes.screen.addeditnote.AddEditNoteScreen
import com.onedev.snapnote.notes.screen.notes.NotesScreen
import com.onedev.snapnote.notes.utils.AppModalDrawer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NoteNavGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    startDestination: String = NoteDestinations.NOTES_ROUTE,
    navActions: NoteNavigationActions = remember(navHostController) {
        NoteNavigationActions(navHostController)
    }
) {
    val currentBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: startDestination

    val navGraph = navHostController.createGraph(startDestination = startDestination) {
        // Note Screen
        composable(
            NoteDestinations.NOTES_ROUTE,
            arguments = listOf(
                navArgument(USER_MESSAGE_ARGS) { type = NavType.IntType; defaultValue = 0 }
            ),
        ) { entry ->
            AppModalDrawer(
                drawerState = drawerState,
                currentRoute = currentRoute,
                navigationActions = navActions
            ) {
                NotesScreen(
                    userMessage = entry.arguments?.getInt(USER_MESSAGE_ARGS) ?: 0,
                    onAddNote = {
                        navActions.navigationToAddEditNote(R.string.add_note, null)
                    },
                    onNoteClick = { note ->
                        navActions.navigationToNoteDetail(note.id)
                    },
                    onUserMessageDisplayed = {
                        entry.arguments?.putInt(USER_MESSAGE_ARGS, 0)
                    },
                    openDrawer = { coroutineScope.launch { drawerState.open() } })
            }
        }
        // Add Edit Note Screen
        composable(
            NoteDestinations.ADD_EDIT_NOTES_ROUTE,
            arguments = listOf(
                navArgument(TITLE_ARG) { type = NavType.IntType },
                navArgument(NOTE_ID_ARG) { type = NavType.StringType; nullable = true }
            )
        ) { entry ->
            val noteId = entry.arguments?.getString(NOTE_ID_ARG)
            AddEditNoteScreen(
                topAppBarTitle = entry.arguments?.getInt(TITLE_ARG)!!,
                onNoteUpdate = {
                    navActions.navigationToNotes(
                        if (noteId == null) ADD_EDIT_RESULT_OK else EDIT_RESULT_OK
                    )
                },
                onBack = { navHostController.popBackStack() })
        }
    }

    NavHost(navController = navHostController, graph = navGraph, modifier = modifier)
}

const val ADD_EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 1
const val DELETE_RESULT_OK = Activity.RESULT_FIRST_USER + 2
const val EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 3
