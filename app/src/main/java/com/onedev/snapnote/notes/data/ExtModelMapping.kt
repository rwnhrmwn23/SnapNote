package com.onedev.snapnote.notes.data

import com.onedev.snapnote.notes.data.source.local.NoteLocal
import com.onedev.snapnote.notes.data.source.network.NoteNetwork
import com.onedev.snapnote.notes.data.source.network.NoteStatus

fun Note.toLocal() = NoteLocal(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted
)

fun List<Note>.toLocal() = map(Note::toLocal)

fun NoteLocal.toExternal() = Note(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted
)

@JvmName("localToExternal")
fun List<NoteLocal>.toExternal() = map(NoteLocal::toExternal)

fun NoteNetwork.toLocal() = NoteLocal(
    id = id,
    title = title,
    description = description,
    isCompleted = (status == NoteStatus.COMPLETE)
)

@JvmName("networkToLocal")
fun List<NoteNetwork>.toLocal() = map(NoteNetwork::toLocal)

fun NoteLocal.toNetwork() = NoteNetwork(
    id = id,
    title = title,
    description = description,
    status = if (isCompleted) NoteStatus.COMPLETE else NoteStatus.ACTIVE
)

fun List<NoteLocal>.toNetwork() = map(NoteLocal::toNetwork)

fun Note.toNetwork() = toLocal().toNetwork()

@JvmName("externalToNetwork")
fun List<Note>.toNetwork() = map(Note::toNetwork)

fun NoteNetwork.toExternal() = toLocal().toExternal()

@JvmName("networkToExternal")
fun List<NoteNetwork>.toExternal() = map(NoteNetwork::toExternal)
