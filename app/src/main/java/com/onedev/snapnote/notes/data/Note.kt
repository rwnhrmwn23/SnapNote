package com.onedev.snapnote.notes.data

data class Note(
    val id: String,
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
) {
    val titleForList: String
        get() = title.ifEmpty { description }

    val isActive
        get() = !isCompleted

    val isEmpty
        get() = title.isEmpty() || description.isEmpty()
}