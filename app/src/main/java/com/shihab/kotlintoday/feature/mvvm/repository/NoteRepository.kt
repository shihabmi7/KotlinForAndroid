package com.shihab.kotlintoday.feature.mvvm.repository

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.shihab.kotlintoday.feature.mvvm.dao.NoteDao
import com.shihab.kotlintoday.feature.mvvm.db.NoteDatabase
import com.shihab.kotlintoday.feature.mvvm.model.Note

class NoteRepository(application: Application) {

    var noteDao: NoteDao
    private var allNotes: LiveData<List<Note>>

    init {
        val nd = NoteDatabase.getInstance(application)
        noteDao = nd.noteDao()
        allNotes = noteDao.getAllNotes()
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
        return allNotes
    }

    class InsertNoteAsync(val noteDao: NoteDao) : AsyncTask<Note?, Void, Void>() {

        override fun doInBackground(vararg params: Note?): Void? {
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