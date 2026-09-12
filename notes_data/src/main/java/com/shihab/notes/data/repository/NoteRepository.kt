package com.shihab.notes.data.repository

import android.content.Context
import com.shihab.notes.data.dao.NoteDao
import com.shihab.notes.data.model.Note
import com.shihab.notes.data.remote.NotesApiService
import com.shihab.notes.data.util.NotesConnectivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

// Single source of truth for Notes data, shared by both UIs (the legacy
// View/data-binding screen and NotesListScreen/AddNoteScreen in
// :notes_compose) via the shared NoteViewModel — neither UI talks to
// NoteDao/NotesApiService directly.
class NoteRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: NotesApiService,
    private val noteDao: NoteDao
) {

    // "Offline-first, server-refresh-when-online" pattern: online, pull from
    // the network, write it into Room, then read back from Room as the
    // single source of truth (so the UI always renders one shape of data
    // regardless of where it came from); offline, just read what's already
    // cached locally. Network/DB failures are swallowed here rather than
    // surfaced to the caller — callers just see whatever Room had.
    suspend fun getAllNotes(): List<Note> {
        val noteList = mutableListOf<Note>()

        try {
            coroutineScope {
                if (NotesConnectivity.isConnected(context)) {
                    val notesFromServer = async { apiService.getNotes() }.await()

                    withContext(Dispatchers.IO) {
                        noteDao.insertAllNote(notesFromServer)
                    }

                    val notesFromDatabase = async { noteDao.getAllNotes() }.await()
                    noteList.addAll(notesFromDatabase)
                } else {
                    val notesFromDatabase =
                        withContext(Dispatchers.IO) { noteDao.getAllNotes() }
                    noteList.addAll(notesFromDatabase)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return noteList
    }

    suspend fun getNotesFromDB(): List<Note> {
        val noteList = mutableListOf<Note>()
        coroutineScope {
            val notesFromDatabase =
                withContext(Dispatchers.Default) { noteDao.getAllNotes() }
            noteList.addAll(notesFromDatabase)
        }
        return noteList
    }

    fun getNotesFromDBByFlow(): Flow<List<Note>> {
        return noteDao.getAllNotesWithFlow()
    }

    suspend fun insert(note: Note) {
        noteDao.insertNote(note)
    }

    suspend fun update(note: Note) {
        coroutineScope {
            noteDao.updateNote(note)
        }
    }

    suspend fun delete(note: Note) {
        coroutineScope {
            noteDao.delete(note)
        }
    }

    suspend fun deleteAllNotes() {
        coroutineScope {
            noteDao.deleteAllNotes()
        }
    }
}
