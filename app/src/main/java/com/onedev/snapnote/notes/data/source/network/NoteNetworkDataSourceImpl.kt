package com.onedev.snapnote.notes.data.source.network

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class NoteNetworkDataSourceImpl @Inject constructor() : NoteNetworkDataSource {

    private val accessMutex = Mutex()
    private var notes = listOf(
        NoteNetwork(
            id = "Jakarta",
            title = "Build monument national",
            description = "this monument for memorizing independence day"
        ),
        NoteNetwork(
            id = "Madrid",
            title = "finish stadium in madrid",
            description = "the biggest stadium in spain"
        )
    )
    override suspend fun loadNotes(): List<NoteNetwork> = accessMutex.withLock {
        delay(SERVICE_LATENCY_IN_MILLIS)
        return notes
    }

    override suspend fun saveNotes(newNotes: List<NoteNetwork>) = accessMutex.withLock {
        delay(SERVICE_LATENCY_IN_MILLIS)
        notes = newNotes
    }
}

private const val SERVICE_LATENCY_IN_MILLIS = 2000L