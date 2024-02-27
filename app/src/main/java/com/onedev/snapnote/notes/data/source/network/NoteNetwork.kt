package com.onedev.snapnote.notes.data.source.network

data class NoteNetwork (
    val id: String,
    val title: String,
    val description: String,
    val priority: Int? = null,
    val status: NoteStatus = NoteStatus.ACTIVE
)

enum class NoteStatus {
    ACTIVE,
    COMPLETE
}