package com.shihab.kotlintoday.feature.mvvm.repository

import android.content.Context
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.shihab.kotlintoday.feature.mvvm.dao.NoteDao
import com.shihab.kotlintoday.feature.mvvm.db.NoteDatabase
import com.shihab.kotlintoday.feature.mvvm.model.Note
import com.shihab.kotlintoday.utility.LogMe

class NoteRepository(context: Context) {

    var noteDao: NoteDao

    init {
        val nd = NoteDatabase.getInstance(context)
        noteDao = nd.noteDao()
    }

    fun insert(note: Note) {
        InsertNoteAsync(noteDao).execute(note)
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

    fun getAllNotes(): LiveData<List<Note>> {
        return noteDao.getAllNotes()
    }

    class InsertNoteAsync(val noteDao: NoteDao) : AsyncTask<Note?, Void, Void>() {

        override fun doInBackground(vararg params: Note?): Void? {
            LogMe.i("note", "" + params[0]!!)
            noteDao.insertNote(params[0]!!)
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