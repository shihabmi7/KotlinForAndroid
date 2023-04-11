package com.shihab.kotlintoday.feature.mvvm.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.shihab.kotlintoday.feature.mvvm.db.NoteDatabase
import com.shihab.kotlintoday.feature.mvvm.model.Note
import com.shihab.kotlintoday.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class NoteDaoTest {

    private lateinit var database: NoteDatabase
    private lateinit var dao: NoteDao

    @get:Rule
    var instantTaskExecutor = InstantTaskExecutorRule()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            NoteDatabase::class.java
        ).allowMainThreadQueries().build() // access room
        dao = database.noteDao()
    }

    @After
    fun close() {
        database.close()
    }

    @Test
    fun insertNote() = runBlocking {
        val note = Note()
        note.title = "TDD in Action"
        note.description = "War with TDD"
        note.priority = "5"
        dao.insertNote(note)
        val allNotes = dao.getAllNotesWithLiveData().getOrAwaitValue()
        // if we don't want to get ta asynchronously

        assertThat(allNotes.contains(note)).isTrue()
    }


    @Test
    fun deleteNote() = runBlocking {
        val note = Note()
        note.title = "TDD in Action"
        note.description = "War with TDD"
        note.priority = "10"

        dao.insertNote(note)
        dao.delete(note)

        val allNotes = dao.getAllNotesWithLiveData().getOrAwaitValue()
        // if we don't want to get ta asynchronously
        assertThat(allNotes).doesNotContain(note)
    }

}