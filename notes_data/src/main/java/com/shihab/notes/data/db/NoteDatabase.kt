package com.shihab.notes.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shihab.notes.data.dao.NoteDao
import com.shihab.notes.data.model.Note

@Database(entities = [Note::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}
