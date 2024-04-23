package com.onedev.snapnote.notes.screen.statistics

import com.onedev.snapnote.notes.data.Note

internal fun getActiveAndCompleteStats(notes: List<Note>): StateResult {
    return if (notes.isEmpty()) {
        StateResult(0f, 0f)
    } else {
        val totalNote = notes.size
        val numberOfActiveNote = notes.count { it.isActive }
        StateResult(
            activeNotePercent = 100f * numberOfActiveNote / notes.size,
            completedNotePercent = 100f * (totalNote - numberOfActiveNote) / notes.size
        )
    }
}

data class StateResult(
    val activeNotePercent: Float,
    val completedNotePercent: Float
)