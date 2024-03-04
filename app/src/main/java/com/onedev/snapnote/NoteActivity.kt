package com.onedev.snapnote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.onedev.snapnote.notes.NoteNavGraph
import com.onedev.snapnote.ui.theme.SnapNoteTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SnapNoteTheme {
                NoteNavGraph()
            }
        }
    }
}