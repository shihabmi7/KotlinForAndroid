package com.shihab.kotlintoday.feature.mvvm.viewmodel

import android.content.Context
import android.content.Intent
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.shihab.kotlintoday.feature.mvvm.model.Note
import com.shihab.kotlintoday.feature.mvvm.repository.NoteRepository
import com.shihab.kotlintoday.feature.mvvm.ui.AddNoteActivity

class NoteViewModel(val context: Context) : ViewModel() {

    var repository: NoteRepository
    var note = ObservableField<Note>()

    init {
        repository = NoteRepository(context)
    }

    fun insert(note: Note) {
        repository.insert(note)
    }

    fun saveNote() {
        // repository.insert(note)
    }

    fun update(note: Note) {
        repository.update(note)

    }

    fun delete(note: Note) {
        repository.delete(note)

    }

    fun deleteAllNotes() {
        repository.deleteAllNotes()
    }

    fun getAllNotes(): LiveData<List<Note>> {
        return repository.getAllNotes()
    }

    fun openAddNoteActivity() {

        context.startActivity(Intent(context, AddNoteActivity::class.java))

    }
}