package com.shihab.kotlintoday.feature.mvvm.repository

import android.content.Context
import android.os.AsyncTask
import com.shihab.kotlintoday.feature.mvvm.dao.NoteDao
import com.shihab.kotlintoday.feature.mvvm.db.NoteDatabase
import com.shihab.kotlintoday.feature.mvvm.model.Note
import com.shihab.kotlintoday.rest.ApiService
import com.shihab.kotlintoday.utility.Connectivity
import com.shihab.kotlintoday.utility.LogMe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NoteRepository @Inject constructor(
    val context: Context,
    private val apiInterface: ApiService
) {

    var noteDao: NoteDao

    init {
        val nd = NoteDatabase.getInstance(context)
        noteDao = nd.noteDao()
    }

    suspend fun getAllNotes(): List<Note> {

        val noteList = mutableListOf<Note>()

        /** This try catch can handle Network issues inside coroutine*/
        try {
            coroutineScope {
                if (Connectivity.isConnected(context)) {
                    //> async works as parallel

                    LogMe.i("NoteRepo", "async-> notesFromServer started")
                    val notesFromServer =
                        async { apiInterface.getNotes() }.await()

                    withContext(Dispatchers.IO){
                        noteDao.insertAllNote(notesFromServer)
                    }

                    LogMe.i("NoteRepo", "async-> notesFromDatabase started")
                    val notesFromDatabase = async { noteDao.getAllNotes() }.await()

                    //noteList.addAll(notesFromServer + notesFromDatabase)
                    noteList.addAll(notesFromDatabase)

                    //noteList.addAll(notesFromServer)
                    LogMe.i("NoteRepo", "Notes From Server + Database Added")

                    /* > if we want to work like series or syncronous task
                    LogMe.i("NoteRepo", "syncronous -> notes From Database started")
                    val noteListFromDatabase = noteDao.getAllNotes()

                    LogMe.i("NoteRepo", "syncronous -> notes From Server started")
                    val noteListFromServer = RetrofitClient.getAPIInterface().getNotes()

                    noteList.addAll(noteListFromDatabase)
                    LogMe.i("NoteRepo", "syncronous > Notes From Database Added")
                    noteList.addAll(noteListFromServer)
                    LogMe.i("NoteRepo", "syncronous > Notes From Server Added")*/

                } else {
                    /** > With context is another type of async
                     ** > Use to return the result of a single task */

                    val noteListFromDatabase =
                        withContext(Dispatchers.IO) { noteDao.getAllNotes() }
                    noteList.addAll(noteListFromDatabase)
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
            noteList.addAll(notesFromDatabase);
        }
        return noteList
    }

    fun getNotesFromDBByFlow() : Flow<List<Note>> {
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