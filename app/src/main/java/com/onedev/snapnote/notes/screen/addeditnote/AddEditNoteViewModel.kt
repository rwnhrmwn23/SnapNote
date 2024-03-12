package com.onedev.snapnote.notes.screen.addeditnote

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onedev.snapnote.R
import com.onedev.snapnote.notes.NoteDestinationArgs
import com.onedev.snapnote.notes.data.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.RuntimeException
import javax.inject.Inject

data class AddEditNoteUiState(
    val title: String = "",
    val description: String = "",
    val isNoteCompleted: Boolean = false,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val isNoteSaved: Boolean = false
)

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val noteId: String? = savedStateHandle[NoteDestinationArgs.NOTE_ID_ARG]

    private val _uiState = MutableStateFlow(AddEditNoteUiState())
    val uiState: StateFlow<AddEditNoteUiState> = _uiState.asStateFlow()

    init {
        if (noteId != null) {
            loadNote(noteId)
        }
    }

    private fun loadNote(noteId: String) {
        _uiState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            noteRepository.getNote(noteId).let { note ->
                if (note != null) {
                    _uiState.update {
                        it.copy(
                            title = note.title,
                            description = note.description,
                            isNoteCompleted = note.isCompleted,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }

    fun saveNote() {
        if (uiState.value.title.isEmpty() || uiState.value.description.isEmpty()) {
            _uiState.update {
                it.copy(userMessage = R.string.empty_note_message)
            }
            return
        }

        if (noteId == null) {
            createNewNote()
        } else {
            updateNote()
        }
    }

    private fun updateNote() {
        if (noteId == null) {
            throw RuntimeException("updateNote() was called but note is new!")
        }
        viewModelScope.launch {
            noteRepository.updateNote(
                noteId = noteId,
                title = uiState.value.title,
                description = uiState.value.description,
            )
            _uiState.update {
                it.copy(isNoteSaved = true)
            }
        }
    }

    private fun createNewNote() = viewModelScope.launch {
        noteRepository.createNote(uiState.value.title, uiState.value.description)
        _uiState.update {
            it.copy(isNoteSaved = true)
        }
    }

    fun snackBarMessageShown() {
        _uiState.update {
            it.copy(userMessage = null)
        }
    }

    fun updateTitle(newTitle: String) {
        _uiState.update {
            it.copy(title = newTitle)
        }
    }

    fun updateDescription(newDescription: String) {
        _uiState.update {
            it.copy(description = newDescription)
        }
    }
}

