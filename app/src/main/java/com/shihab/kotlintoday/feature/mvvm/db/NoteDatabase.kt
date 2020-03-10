package com.shihab.kotlintoday.feature.mvvm.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.shihab.kotlintoday.feature.mvvm.dao.NoteDao
import com.shihab.kotlintoday.feature.mvvm.model.Note

@Database(entities = [Note::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {

        fun getInstance(context: Context): NoteDatabase {
            val instance: NoteDatabase

            instance =
                Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_db"
                )
                    .build()

            return instance
        }

    }

    //https://www.youtube.com/watch?v=0cg09tlAAQ0&list=PLrnPJCHvNZuDihTpkRs6SpZhqgBqPU118&index=3
}