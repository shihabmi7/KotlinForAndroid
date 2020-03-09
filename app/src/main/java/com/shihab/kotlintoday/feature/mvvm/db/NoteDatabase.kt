package com.shihab.kotlintoday.feature.mvvm.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.shihab.kotlintoday.feature.mvvm.dao.NoteDao
import com.shihab.kotlintoday.feature.mvvm.model.Note

@Database(entities = [Note::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {

    abstract var instance: NoteDatabase

    abstract fun noteDao(): NoteDao

    fun getInstance(context: Context): NoteDatabase {

        instance =
            Room.databaseBuilder(context.applicationContext, NoteDatabase::class.java, "note_db")
                .build()

        return instance
    }

}