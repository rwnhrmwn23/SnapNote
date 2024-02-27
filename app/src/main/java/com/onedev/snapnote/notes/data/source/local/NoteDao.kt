package com.onedev.snapnote.notes.data.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM note")
    fun observeAll(): Flow<List<NoteLocal>>

    @Query("SELECT * FROM note WHERE id = :noteId")
    fun observeById(noteId: String): Flow<NoteLocal>

    @Query("SELECT * FROM note")
    suspend fun getAll(): List<NoteLocal>

    @Query("SELECT * FROM note WHERE id = :noteId")
    suspend fun getById(noteId: String): NoteLocal?

    @Upsert
    suspend fun upsert(note: NoteLocal)

    @Upsert
    suspend fun upsertAll(note: List<NoteLocal>)

    @Query("UPDATE note SET isCompleted = :completed WHERE id = :noteId")
    suspend fun updateCompleted(noteId: String, completed: Boolean)

    @Query("DELETE FROM note WHERE id = :noteId")
    suspend fun deleteById(noteId: String): Int

    @Query("DELETE FROM note")
    suspend fun deleteAll()

    @Query("DELETE FROM note WHERE isCompleted = 1")
    suspend fun deleteCompleted(): Int
}