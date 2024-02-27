package com.onedev.snapnote.notes.di

import android.content.Context
import androidx.room.Room
import com.onedev.snapnote.notes.data.NoteRepository
import com.onedev.snapnote.notes.data.NoteRepositoryImpl
import com.onedev.snapnote.notes.data.source.local.NoteDao
import com.onedev.snapnote.notes.data.source.local.NoteDatabase
import com.onedev.snapnote.notes.data.source.network.NoteNetworkDataSource
import com.onedev.snapnote.notes.data.source.network.NoteNetworkDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindNoteRepository(repositoryImpl: NoteRepositoryImpl): NoteRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Singleton
    @Binds
    abstract fun bindNetworkDataSource(dataSource: NoteNetworkDataSourceImpl): NoteNetworkDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): NoteDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            NoteDatabase::class.java,
            "Notes.db"
        ).build()
    }

    @Provides
    fun provideNoteDao(database: NoteDatabase): NoteDao = database.noteDao()
}