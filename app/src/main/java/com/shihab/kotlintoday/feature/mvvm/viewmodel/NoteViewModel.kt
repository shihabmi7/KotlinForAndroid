package com.shihab.kotlintoday.feature.mvvm.viewmodel

import android.text.TextUtils
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
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

    private var _notes = MutableLiveData<List<Note>>()
    private val _isAddNotesClicked = MutableLiveData<Boolean>()
    val isAddNotesClicked: LiveData<Boolean> = _isAddNotesClicked
    val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message
    var isLoading = ObservableBoolean()
    val note = Note()

    fun saveNote() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (checkValidation(note)) {
                    repository.insert(note)
                    _message.postValue("Successfully Inserted")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getAllNotes() {
        isLoading.set(true)
        viewModelScope.launch(Dispatchers.IO) {
            val mutableLiveData = mutableListOf<Note>()
            mutableLiveData.addAll(repository.getAllNotes())
            _notes.postValue(mutableLiveData)
            isLoading.set(false)
        }
    }

    fun getNotes(): LiveData<List<Note>> = _notes

    fun addNotesClicked() {
        _isAddNotesClicked.value = true
    }

    private fun checkValidation(note: Note): Boolean {

        var value = true

        if (TextUtils.isEmpty(note.title)) {
            _message.postValue("Title is empty...")
            value = false
            return value
        }

        if (TextUtils.isEmpty(note.description)) {
            _message.postValue("Description is empty...")
            value = false
            return value
        }

        if (TextUtils.isEmpty(note.priority)) {
            _message.postValue("priority is empty...")
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