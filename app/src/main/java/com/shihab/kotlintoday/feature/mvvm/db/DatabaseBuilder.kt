package com.shihab.kotlintoday.feature.mvvm.db

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseBuilder {

    private var INSTANCE: NoteDatabase? = null

    fun getInstance(context: Context): NoteDatabase {
        if (INSTANCE == null) {
            synchronized(NoteDatabase::class) {
                INSTANCE = buildRoomDB(context)
            }
        }
        return INSTANCE!!
    }

    private fun buildRoomDB(context: Context) =
        Room.databaseBuilder(
            context.applicationContext,
            NoteDatabase::class.java,
            "note_db"
        ).addCallback(roomCallback).build()

    private val roomCallback = object : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { PopulateDbAsyncTask(it).execute() }
        }
    }

    private class PopulateDbAsyncTask(val db: NoteDatabase) :
        AsyncTask<Void?, Void?, Void?>() {

        override fun doInBackground(vararg voids: Void?): Void? {
            setPredefinedDB()
            return null
        }

        private fun setPredefinedDB() {
            /*db.noteDao().insertNote(Note(1, "Wake Up", "wake up early and pray...", "1"))
            db.noteDao().insertNote(
                Note(
                    2,
                    "Meeting with Tech Team",
                    "Important about upcoming sprint",
                    "2"
                )
            )
            db.noteDao().insertNote(Note(3, "Make an Mvvm Demo", "with kotlin and rx java", "3"))*/
        }
    }
}