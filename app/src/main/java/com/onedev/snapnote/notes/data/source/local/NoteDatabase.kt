package com.onedev.snapnote.notes.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [NoteLocal::class], version = 1, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}