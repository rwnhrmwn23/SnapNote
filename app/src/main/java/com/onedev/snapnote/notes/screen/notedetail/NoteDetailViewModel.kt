package com.onedev.snapnote.notes.screen.notedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onedev.snapnote.R
import com.onedev.snapnote.notes.NoteDestinationArgs
import com.onedev.snapnote.notes.data.Note
import com.onedev.snapnote.notes.data.NoteRepository
import com.onedev.snapnote.notes.utils.Async
import com.onedev.snapnote.notes.utils.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NoteDetailUiState(
    val note: Note? = null,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val isNoteDeleted: Boolean = false
)

@HiltViewModel
class NoteDetailViewModel @Inject constructor (
    private val noteRepository: NoteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val noteId: String = savedStateHandle[NoteDestinationArgs.NOTE_ID_ARG]!!
    private val _userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val _isLoading = MutableStateFlow(false)
    private val _isNoteDeleted = MutableStateFlow(false)
    private val _noteAsync = noteRepository.getNoteStream(noteId)
        .map { handleNote(it) }
        .catch { emit(Async.Error(R.string.loading_notes_error)) }

    val uiState: StateFlow<NoteDetailUiState> = combine(
        _userMessage, _isLoading, _isNoteDeleted, _noteAsync
    ) { userMessage, isLoading, isNoteDeleted, noteAsync ->
        when (noteAsync) {
            Async.Loading -> {
                NoteDetailUiState(isLoading = true)
            }
            is Async.Error -> {
                NoteDetailUiState(
                    userMessage = noteAsync.errorMessage,
                    isNoteDeleted = isNoteDeleted
                )
            }
            is Async.Success -> {
                NoteDetailUiState(
                    note = noteAsync.data,
                    isLoading = isLoading,
                    userMessage = userMessage,
                    isNoteDeleted = isNoteDeleted
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = NoteDetailUiState(isLoading = false)
    )

    fun deleteNote() = viewModelScope.launch {
        noteRepository.deleteNote(noteId)
        _isNoteDeleted.value = true
    }

    fun setCompleted(completed: Boolean) = viewModelScope.launch {
        val note = uiState.value.note ?: return@launch
        if (completed) {
            noteRepository.completeNote(note.id)
            showSnackBarMessage(R.string.note_marked_complete)
        } else {
            noteRepository.activeNote(note.id)
            showSnackBarMessage(R.string.note_marked_active)
        }
    }

    fun refresh() {
        _isLoading.value = false
        viewModelScope.launch {
            noteRepository.refreshNote(noteId)
            _isLoading.value = true
        }
    }

    fun snackBarMessageShown() {
        _userMessage.value = null
    }

    private fun showSnackBarMessage(message: Int) {
        _userMessage.value = message
    }

    private fun handleNote(note: Note?): Async<Note?> {
        if (note == null) {
            return Async.Error(R.string.note_not_found)
        }
        return Async.Success(note)
    }
}