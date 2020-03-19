package com.shihab.kotlintoday.feature.mvvm.viewmodel

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shihab.kotlintoday.feature.mvvm.model.Note
import com.shihab.kotlintoday.feature.mvvm.repository.NoteRepository
import com.shihab.kotlintoday.feature.mvvm.ui.AddNoteActivity

class NoteViewModel(val context: Context) : ViewModel() {

    var repository: NoteRepository
    val note = Note()
    val message = MutableLiveData<String>()

    init {
        repository = NoteRepository(context)
    }

    fun insert(note: Note) {
        repository.insert(note)
    }

    fun saveNote() {
        if (checkValidation(note)) {
            repository.insert(note)
        }
    }

    private fun checkValidation(note: Note): Boolean {

        var value = true

        if (TextUtils.isEmpty(note.title)) {
            message.postValue("Title is empty...")
            value = false
            return value
        }

        if (TextUtils.isEmpty(note.description)) {
            message.postValue("Description is empty...")
            value = false
            return value
        }

        if (TextUtils.isEmpty(note.priority)) {
            message.postValue("priority is empty...")
            value = false
            return value
        }

        return value
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