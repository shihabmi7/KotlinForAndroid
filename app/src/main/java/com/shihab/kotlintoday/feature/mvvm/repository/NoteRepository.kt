package com.shihab.kotlintoday.feature.mvvm.repository

import android.content.Context
import android.os.AsyncTask
import com.shihab.kotlintoday.feature.mvvm.dao.NoteDao
import com.shihab.kotlintoday.feature.mvvm.db.NoteDatabase
import com.shihab.kotlintoday.feature.mvvm.model.Note
import com.shihab.kotlintoday.rest.RetrofitClient
import com.shihab.kotlintoday.utility.Connectivity
import com.shihab.kotlintoday.utility.LogMe
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class NoteRepository(val context: Context) {

    var noteDao: NoteDao

    init {
        val nd = NoteDatabase.getInstance(context)
        noteDao = nd.noteDao()
    }

    suspend fun getAllNotes(): List<Note> {

        var noteList = mutableListOf<Note>()

        coroutineScope {
            if (Connectivity.isConnected(context)) {
                // async works as parallel
                /*val noteListFromServer = async { RetrofitClient.getAPIInterface().getNotes() }.await()
                val noteListFromDatabase = async { noteDao.getAllNotes() }.await()
                noteList.addAll(noteListFromDatabase)
                noteList.addAll(noteListFromServer)*/

                // if we want to work like series
                val noteListFromDatabase = noteDao.getAllNotes()
                val noteListFromServer =  RetrofitClient.getAPIInterface().getNotes()
                noteList.addAll(noteListFromDatabase)
                noteList.addAll(noteListFromServer)

            } else {
                val noteListFromDatabase = async { noteDao.getAllNotes() }.await()
                noteList.addAll(noteListFromDatabase)
            }
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