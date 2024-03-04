package com.onedev.snapnote.notes.screen.notes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onedev.snapnote.R
import com.onedev.snapnote.notes.ADD_EDIT_RESULT_OK
import com.onedev.snapnote.notes.DELETE_RESULT_OK
import com.onedev.snapnote.notes.EDIT_RESULT_OK
import com.onedev.snapnote.notes.data.Note
import com.onedev.snapnote.notes.data.NoteRepository
import com.onedev.snapnote.notes.utils.Async
import com.onedev.snapnote.notes.utils.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotesUiState(
    val items: List<Note> = emptyList(),
    val isLoading: Boolean = false,
    val filteringUiInfo: FilteringUiInfo = FilteringUiInfo(),
    val userMessage: Int? = null
)

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _savedFilterType =
        savedStateHandle.getStateFlow(NOTES_FILTER_SAVED_STATE_KEY, NotesFilterType.ALL_NOTES)

    private val _filteringUiInfo =
        _savedFilterType.map { getFilterUiInfo(it) }.distinctUntilChanged()
    private val _userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val _isLoading = MutableStateFlow(false)
    private val _filteredNotesAsync =
        combine(noteRepository.getNotesStream(), _savedFilterType) { notes, type ->
            filterNotes(notes, type)
        }.map {
            Async.Success(it)
        }.catch<Async<List<Note>>> {
            emit(Async.Error(R.string.loading_note_error))
        }

    val uiState: StateFlow<NotesUiState> = combine(
        _filteringUiInfo, _isLoading, _userMessage, _filteredNotesAsync
    ) { filteringUiInfo, isLoading, userMessage, notesAsync ->
        when (notesAsync) {
            is Async.Loading -> {
                NotesUiState(isLoading = true)
            }

            is Async.Error -> {
                NotesUiState(userMessage = notesAsync.errorMessage)
            }

            is Async.Success -> {
                NotesUiState(
                    items = notesAsync.data,
                    filteringUiInfo = filteringUiInfo,
                    isLoading = isLoading,
                    userMessage = userMessage
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = NotesUiState(isLoading = true)
    )

    fun setFiltering(requestType: NotesFilterType) {
        savedStateHandle[NOTES_FILTER_SAVED_STATE_KEY] = requestType
    }

    fun clearCompletedNotes() {
        viewModelScope.launch {
            noteRepository.clearCompletedNote()
            showSnackBarMessage(R.string.completed_notes_cleared)
            refresh()
        }
    }

    fun completeNote(note: Note, completed: Boolean) = viewModelScope.launch {
        if (completed) {
            noteRepository.completeNote(note.id)
            showSnackBarMessage(R.string.note_marked_complete)
        } else {
            noteRepository.activeNote(note.id)
            showSnackBarMessage(R.string.note_marked_active)
        }
    }

    fun showEditResultMessage(result: Int) {
        when (result) {
            EDIT_RESULT_OK -> showSnackBarMessage(R.string.successfully_saved_note_message)
            ADD_EDIT_RESULT_OK -> showSnackBarMessage(R.string.successfully_added_note_message)
            DELETE_RESULT_OK -> showSnackBarMessage(R.string.successfully_deleted_note_message)
        }
    }

    fun snackBarMessageShown() {
        _userMessage.value = null
    }

    private fun showSnackBarMessage(message: Int) {
        _userMessage.value = message
    }

    fun refresh() {
        _isLoading.value = true
        viewModelScope.launch {
            noteRepository.refresh()
            _isLoading.value = false
        }
    }

    private fun filterNotes(notes: List<Note>, filteringType: NotesFilterType): List<Note> {
        val notesToShow = ArrayList<Note>()
        for (note in notes) {
            when (filteringType) {
                NotesFilterType.ALL_NOTES -> notesToShow.add(note)
                NotesFilterType.ACTIVE_NOTES -> {
                    if (note.isActive) notesToShow.add(note)
                }

                NotesFilterType.COMPLETED_NOTES -> {
                    if (note.isCompleted) notesToShow.add(note)
                }
            }
        }
        return notesToShow
    }

    private fun getFilterUiInfo(requestType: NotesFilterType): FilteringUiInfo =
        when (requestType) {
            NotesFilterType.ALL_NOTES -> {
                FilteringUiInfo(
                    R.string.label_all,
                    R.string.no_notes_all,
                    R.drawable.logo_no_fill,
                )
            }

            NotesFilterType.ACTIVE_NOTES -> {
                FilteringUiInfo(
                    R.string.label_active,
                    R.string.no_notes_active,
                    R.drawable.ic_check_circle_96dp,
                )
            }

            NotesFilterType.COMPLETED_NOTES -> {
                FilteringUiInfo(
                    R.string.label_completed,
                    R.string.no_notes_completed,
                    R.drawable.ic_verified_user_96dp,
                )
            }
        }

}

const val NOTES_FILTER_SAVED_STATE_KEY = "NOTES_FILTER_SAVED_STATE_KEY"

data class FilteringUiInfo(
    val currentFilteringLabel: Int = R.string.label_all,
    val noNotesLabel: Int = R.string.no_notes_all,
    val noNotesIconRes: Int = R.drawable.logo_no_fill,
)