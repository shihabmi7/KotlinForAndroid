package com.shihab.kotlintoday.feature.mvvm.dao

import androidx.room.*
import com.shihab.kotlintoday.feature.mvvm.model.Note

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNote(notes: List<Note>)

    @Update
    fun updateNote(note: Note)

    @Delete
    fun delete(note: Note)

    @Query("DELETE FROM note_table")
    fun deleteAllNotes()

    @Query("SELECT * FROM note_table ORDER BY priority desc")
    suspend fun getAllNotes(): List<Note>

}