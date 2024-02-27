package com.onedev.snapnote.notes.data

import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotesStream(): Flow<List<Note>>

    suspend fun getNotes(forceUpdate: Boolean = false): List<Note>

    suspend fun refresh()

    fun getNoteStream(noteId: String): Flow<Note?>

    suspend fun getNote(noteId: String, forceUpdate: Boolean = false): Note?

    suspend fun refreshNote(noteId: String)

    suspend fun createNote(title: String, description: String): String

    suspend fun updateNote(noteId: String, title: String, description: String)

    suspend fun completeNote(noteId: String)

    suspend fun activeNote(noteId: String)

    suspend fun clearCompletedNote()

    suspend fun deleteAllNote()

    suspend fun deleteNote(noteId: String)
}