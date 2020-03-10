package com.shihab.kotlintoday.feature.mvvm.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.shihab.kotlintoday.feature.mvvm.model.Note
import com.shihab.kotlintoday.feature.mvvm.repository.NoteRepository

class NoteViewModel(application: Application): ViewModel() {

    private var repository: NoteRepository = NoteRepository(application)

    fun insert(note: Note) {
        repository.insert(note)
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
}