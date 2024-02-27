package com.onedev.snapnote.notes.data

import com.onedev.snapnote.notes.data.source.local.NoteDao
import com.onedev.snapnote.notes.data.source.network.NoteNetworkDataSource
import com.onedev.snapnote.notes.di.ApplicationScope
import com.onedev.snapnote.notes.di.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val networkDataSource: NoteNetworkDataSource,
    private val localDataSource: NoteDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope
) : NoteRepository {
    override fun getNotesStream(): Flow<List<Note>> {
        return localDataSource.observeAll().map { notes ->
            withContext(dispatcher) {
                notes.toExternal()
            }
        }
    }

    override suspend fun getNotes(forceUpdate: Boolean): List<Note> {
        if (forceUpdate) {
            refresh()
        }
        return withContext(dispatcher) {
            localDataSource.getAll().toExternal()
        }
    }

    override suspend fun refresh() {
        withContext(dispatcher) {
            val remoteNotes = networkDataSource.loadNotes()
            localDataSource.deleteAll()
            localDataSource.upsertAll(remoteNotes.toLocal())
        }
    }

    override fun getNoteStream(noteId: String): Flow<Note?> {
        return localDataSource.observeById(noteId).map { it.toExternal() }
    }

    override suspend fun getNote(noteId: String, forceUpdate: Boolean): Note? {
        if (forceUpdate) {
            refresh()
        }
        return localDataSource.getById(noteId)?.toExternal()
    }

    override suspend fun refreshNote(noteId: String) {
        refresh()
    }

    override suspend fun createNote(title: String, description: String): String {
        val noteId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val note = Note(
            id = noteId,
            title = title,
            description = description
        )
        localDataSource.upsert(note.toLocal())
        saveNoteToNetwork()
        return noteId
    }

    override suspend fun updateNote(noteId: String, title: String, description: String) {
        val note = getNote(noteId)?.copy(
            title = title,
            description = description
        ) ?: throw Exception("Note (id $noteId) not found")

        localDataSource.upsert(note.toLocal())
        saveNoteToNetwork()
    }

    override suspend fun completeNote(noteId: String) {
        localDataSource.updateCompleted(noteId = noteId, completed = true)
    }

    override suspend fun activeNote(noteId: String) {
        localDataSource.updateCompleted(noteId = noteId, completed = false)
    }

    override suspend fun clearCompletedNote() {
        localDataSource.deleteCompleted()
        saveNoteToNetwork()
    }

    override suspend fun deleteAllNote() {
        localDataSource.deleteAll()
        saveNoteToNetwork()
    }

    override suspend fun deleteNote(noteId: String) {
        localDataSource.deleteById(noteId)
        saveNoteToNetwork()
    }

    private fun saveNoteToNetwork() {
        scope.launch {
            try {
                val localNotes = localDataSource.getAll()
                val networkNotes = withContext(dispatcher) {
                    localNotes.toNetwork()
                }
                networkDataSource.saveNotes(networkNotes)
            } catch (_: Exception) {
                // In a real app you'd handle the exception e.g. by exposing a `networkStatus` flow
                // to an app level UI state holder which could then display a Toast message.
            }
        }
    }
}