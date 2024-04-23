package com.onedev.snapnote.notes.screen.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onedev.snapnote.R
import com.onedev.snapnote.notes.data.Note
import com.onedev.snapnote.notes.data.NoteRepository
import com.onedev.snapnote.notes.utils.Async
import com.onedev.snapnote.notes.utils.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatisticUiState(
    val isEmpty: Boolean = false,
    val isLoading: Boolean = false,
    val activeNotesPercent: Float = 0f,
    val completedNotesPercent: Float = 0f
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val noteRepository: NoteRepository
): ViewModel() {
    val uiState: StateFlow<StatisticUiState> =
        noteRepository.getNotesStream()
            .map { Async.Success(it) }
            .catch<Async<List<Note>>> { emit(Async.Error(R.string.loading_notes_error)) }
            .map { noteAsync -> produceStatisticsUiState(noteAsync) }
            .stateIn(
                scope = viewModelScope,
                started = WhileUiSubscribed,
                initialValue = StatisticUiState(isLoading = true)
            )

    fun refresh() {
        viewModelScope.launch {
            noteRepository.refresh()
        }
    }

    private fun produceStatisticsUiState(noteLoad: Async<List<Note>>) =
        when(noteLoad) {
            is Async.Loading -> {
                StatisticUiState(isLoading = true, isEmpty = false)
            }
            is Async.Error -> {
                StatisticUiState(isEmpty = true, isLoading = false)
            }
            is Async.Success -> {
                val stats = getActiveAndCompleteStats(noteLoad.data)
                StatisticUiState(
                    isEmpty = noteLoad.data.isEmpty(),
                    activeNotesPercent = stats.activeNotePercent,
                    completedNotesPercent = stats.completedNotePercent,
                    isLoading = false
                )
            }
        }
}