package com.shihab.notes.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.shihab.notes.data.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNote(notes: List<Note>)

    // NOTE: unlike the inserts above, these two aren't `suspend`. Room still
    // runs them off the main thread only if the *caller* explicitly does so
    // (see NoteRepository.update()/delete() wrapping these in
    // Dispatchers.IO) — Room itself won't enforce it here the way it does
    // for a `suspend` DAO method. Worth normalizing to `suspend fun` for
    // consistency with the rest of the DAO.
    @Update
    fun updateNote(note: Note)

    @Delete
    fun delete(note: Note)

    @Query("DELETE FROM note_table")
    fun deleteAllNotes()

    // Same query, three return types, three different consumers:
    // - List<Note> (suspend): one-shot read, e.g. after a delete completes.
    // - Flow<List<Note>>: cold reactive stream — used by the "Kotlin Flow"
    //   demo variant of the legacy NoteActivity.
    // - LiveData<List<Note>>: used by the XML data-binding screen, and
    //   bridged into Compose state via observeAsState() in NotesListScreen.
    // Room regenerates the query result and re-emits on the Flow/LiveData
    // versions automatically whenever note_table changes underneath them.
    @Query("SELECT * FROM note_table ORDER BY priority desc")
    suspend fun getAllNotes(): List<Note>

    @Query("SELECT * FROM note_table ORDER BY priority desc")
    fun getAllNotesWithFlow(): Flow<List<Note>>

    @Query("SELECT * FROM note_table ORDER BY priority desc")
    fun getAllNotesWithLiveData(): LiveData<List<Note>>
}
