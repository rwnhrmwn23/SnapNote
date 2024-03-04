package com.onedev.snapnote.notes

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.onedev.snapnote.notes.NoteDestinationArgs.NOTE_ID_ARG
import com.onedev.snapnote.notes.NoteDestinationArgs.TITLE_ARG
import com.onedev.snapnote.notes.NoteDestinationArgs.USER_MESSAGE_ARGS
import com.onedev.snapnote.notes.NoteScreens.ADD_EDIT_NOTES_SCREEN
import com.onedev.snapnote.notes.NoteScreens.NOTES_DETAIL_SCREEN
import com.onedev.snapnote.notes.NoteScreens.NOTES_SCREEN
import com.onedev.snapnote.notes.NoteScreens.STATISTICS_SCREEN

private object NoteScreens {
    const val NOTES_SCREEN = "NOTES_SCREEN"
    const val NOTES_DETAIL_SCREEN = "NOTES_DETAIL_SCREEN"
    const val ADD_EDIT_NOTES_SCREEN = "ADD_EDIT_NOTES_SCREEN"
    const val STATISTICS_SCREEN = "STATISTICS_SCREEN"
}

object NoteDestinationArgs {
    const val USER_MESSAGE_ARGS = "USER_MESSAGE_ARGS"
    const val NOTE_ID_ARG = "NOTE_ID_ARG"
    const val TITLE_ARG = "TITLE_ARG"
}

object NoteDestinations {
    const val NOTES_ROUTE = "$NOTES_SCREEN?$USER_MESSAGE_ARGS={$USER_MESSAGE_ARGS}"
    const val NOTES_DETAIL_ROUTE = "$NOTES_DETAIL_SCREEN/{$NOTE_ID_ARG}"
    const val ADD_EDIT_NOTES_ROUTE = "$ADD_EDIT_NOTES_SCREEN/{$TITLE_ARG}?$NOTE_ID_ARG={$NOTE_ID_ARG}"
    const val STATISTICS_ROUTE = STATISTICS_SCREEN
}

class NoteNavigationActions(private val navHostController: NavHostController) {
    fun navigationToNotes(userMessage: Int = 0) {
        val navigationFromDrawer = userMessage == 0
        navHostController.navigate(
            NOTES_SCREEN.let {
                if (userMessage != 0) "$it?$USER_MESSAGE_ARGS=$userMessage" else it
            }
        ) {
            popUpTo(navHostController.graph.findStartDestination().id) {
                inclusive = !navigationFromDrawer
                saveState = navigationFromDrawer
            }
            launchSingleTop = true
            restoreState = navigationFromDrawer
        }
    }

    fun navigationToNoteDetail(noteId: String) {
        navHostController.navigate("$NOTES_DETAIL_SCREEN/$noteId")
    }

    fun navigationToAddEditNote(title: Int, noteId: String?) {
        navHostController.navigate(
            "$ADD_EDIT_NOTES_SCREEN/$title".let {
                if (noteId != null) "$it?$NOTE_ID_ARG=$noteId" else it
            }
        )
    }

    fun navigationToStatistics() {
        navHostController.navigate(NoteDestinations.STATISTICS_ROUTE) {
            popUpTo(navHostController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}