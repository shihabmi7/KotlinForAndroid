package com.shihab.notes.data.viewmodel

import android.text.TextUtils
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shihab.notes.data.model.Note
import com.shihab.notes.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(private val repository: NoteRepository) :
    ViewModel() {

    private val _notes = MutableLiveData<List<Note>>()
    private val _isAddNotesClicked = MutableLiveData<Boolean>()
    val isAddNotesClicked: LiveData<Boolean> = _isAddNotesClicked
    private val _showMessage = MutableLiveData<String>()
    val message: LiveData<String> = _showMessage
    var isLoading = ObservableBoolean()
    val note = Note()

    fun getNotes(): LiveData<List<Note>> = _notes

    fun saveNote() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (checkValidation(note)) {
                    repository.insert(note)
                    _showMessage.postValue("Successfully Inserted")
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

    fun getAllNotesFromFlow() = repository.getNotesFromDBByFlow()

    fun addNotesClicked() {
        _isAddNotesClicked.value = true
    }

    private fun checkValidation(note: Note): Boolean {
        var value = true

        if (TextUtils.isEmpty(note.title)) {
            _showMessage.postValue("Title is empty...")
            value = false
            return value
        }

        if (TextUtils.isEmpty(note.description)) {
            _showMessage.postValue("Description is empty...")
            value = false
            return value
        }

        if (TextUtils.isEmpty(note.priority)) {
            _showMessage.postValue("priority is empty...")
            value = false
            return value
        }

        return value
    }

    suspend fun update(note: Note) {
        repository.update(note)
    }

    // Deliberately one coroutine, not two separate `launch` calls for the
    // delete and the re-fetch: the original code launched them independently
    // on different dispatchers, which raced — the re-fetch could complete
    // and post a stale list before the delete had actually written to Room,
    // so the UI (both the Compose screen and the legacy swipe-to-delete)
    // would occasionally show the "deleted" item still sitting in the list
    // for a beat, or in the worst case never refresh it away without a
    // second trigger. Sequencing withContext(IO) then the re-fetch in the
    // same coroutine guarantees the delete is committed before we read.
    fun delete(aNote: Note) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.delete(aNote)
            }
            _notes.value = repository.getNotesFromDB()
        }
    }

    fun deleteAllNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllNotes()
        }
    }
}
