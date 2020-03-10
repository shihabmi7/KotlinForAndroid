package com.shihab.kotlintoday.feature.mvvm.db

import android.content.Context
import android.os.AsyncTask
import androidx.annotation.NonNull
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.shihab.kotlintoday.feature.mvvm.dao.NoteDao
import com.shihab.kotlintoday.feature.mvvm.model.Note


@Database(entities = [Note::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    lateinit var roomCallback: RoomDatabase.Callback

    companion object {
        lateinit var instance: NoteDatabase
        lateinit var roomCallback: RoomDatabase.Callback

        fun getInstance(context: Context): NoteDatabase {

            instance =
                Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_db"
                ).addCallback(roomCallback)
                    .build()

            return instance
        }

    }



    private class PopulateDbAsyncTask(val noteDao: NoteDao) :
        AsyncTask<Void?, Void?, Void?>() {

        override fun doInBackground(vararg voids: Void?): Void? {
            noteDao.insertNote(Note("Title 1", "Description 1", 1))
            noteDao.insertNote(Note("Title 2", "Description 2", 2))
            noteDao.insertNote(Note("Title 3", "Description 3", 3))
            return null
        }
    }


    //https://www.youtube.com/watch?v=0cg09tlAAQ0&list=PLrnPJCHvNZuDihTpkRs6SpZhqgBqPU118&index=3
}