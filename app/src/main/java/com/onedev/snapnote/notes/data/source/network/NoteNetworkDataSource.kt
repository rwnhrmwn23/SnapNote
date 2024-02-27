package com.onedev.snapnote.notes.data.source.network

interface NoteNetworkDataSource {
    suspend fun loadNotes(): List<NoteNetwork>

    suspend fun saveNotes(newNotes: List<NoteNetwork>)
}