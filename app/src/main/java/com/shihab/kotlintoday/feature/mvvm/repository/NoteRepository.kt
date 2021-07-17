package com.shihab.kotlintoday.feature.mvvm.repository

import android.content.Context
import android.os.AsyncTask
import com.shihab.kotlintoday.feature.mvvm.dao.NoteDao
import com.shihab.kotlintoday.feature.mvvm.db.NoteDatabase
import com.shihab.kotlintoday.feature.mvvm.model.Note
import com.shihab.kotlintoday.rest.ApiService
import com.shihab.kotlintoday.utility.Connectivity
import com.shihab.kotlintoday.utility.LogMe
import kotlinx.coroutines.*
import javax.inject.Inject

class NoteRepository(val context: Context, val apiInterface: ApiService) {

    var noteDao: NoteDao

    init {
        val nd = NoteDatabase.getInstance(context)
        noteDao = nd.noteDao()
    }

    suspend fun getAllNotes(): List<Note> {

        var noteList = mutableListOf<Note>()

        /** This try catch can handle Network issues inside coroutine*/
        try {
            coroutineScope {
                if (Connectivity.isConnected(context)) {
                    //> async works as parallel

                    LogMe.i("NoteRepo", "async-> notesFromServer started")
                    val notesFromServer =
                        async { apiInterface.getNotes() }.await()

                    LogMe.i("NoteRepo", "async-> notesFromDatabase started")
                    val notesFromDatabase = async { noteDao.getAllNotes() }.await()

                    val multipleCoroutine = listOf(
                        async { apiInterface.getNotes() },
                        async { noteDao.getAllNotes() }
                    )

                    multipleCoroutine.awaitAll()

                    noteList.addAll(notesFromServer)
                    LogMe.i("NoteRepo", "Notes From Server Added")

                    noteList.addAll(notesFromDatabase)
                    LogMe.i("NoteRepo", "Notes From Database Added")

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

    suspend fun insert(note: Note) {
        noteDao.insertNote(note)
    }

    fun update(note: Note) {
        UpdateNoteAsync(noteDao).execute(note)
    }

    fun delete(note: Note) {
        DeleteNoteAsync(noteDao).execute(note)
    }

    fun deleteAllNotes() {
        DeleteAllNoteAsync(noteDao).execute()
    }

    class InsertNoteAsync(val noteDao: NoteDao) : AsyncTask<Note?, Void, Void>() {

        override fun doInBackground(vararg params: Note?): Void? {
            LogMe.i("note", "" + params[0]!!)
            //noteDao.insertNote(params[0]!!)
            return null
        }
    }

    class UpdateNoteAsync(val noteDao: NoteDao) : AsyncTask<Note?, Void, Void>() {

        override fun doInBackground(vararg params: Note?): Void? {
            noteDao.updateNote(params[0]!!)
            return null
        }
    }

    class DeleteNoteAsync(val noteDao: NoteDao) : AsyncTask<Note?, Void, Void>() {

        override fun doInBackground(vararg params: Note?): Void? {
            noteDao.delete(params[0]!!)
            return null
        }
    }

    class DeleteAllNoteAsync(val noteDao: NoteDao) : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void?): Void? {
            noteDao.deleteAllNotes()
            return null
        }
    }
}