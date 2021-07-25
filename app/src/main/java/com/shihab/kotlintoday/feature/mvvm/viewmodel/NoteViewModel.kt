package com.shihab.kotlintoday.feature.mvvm.viewmodel

import android.text.TextUtils
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shihab.kotlintoday.feature.mvvm.model.Note
import com.shihab.kotlintoday.feature.mvvm.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(private var repository: NoteRepository) :
    ViewModel() {

    val note = Note()
    private var notes = MutableLiveData<List<Note>>()
    val message = MutableLiveData<String>()
    val isAddNotesClicked = MutableLiveData<Boolean>()
    var isLoading = ObservableBoolean()

    init {
        getAllNotes()
    }

    fun saveNote() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (checkValidation(note)) {
                    repository.insert(note)
                    message.postValue("Successfully Inserted")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getAllNotes() {
        isLoading.set(true)
        viewModelScope.launch(Dispatchers.IO) {
            val mutableLiveData = mutableListOf<Note>()
            mutableLiveData.addAll(repository.getAllNotes())
            notes.postValue(mutableLiveData)
            isLoading.set(false)
        }
    }

    fun getNotes(): MutableLiveData<List<Note>> = notes

    fun addNotesClicked() {
        isAddNotesClicked.value = true
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
}