package com.shihab.kotlintoday.feature.mvvm.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.shihab.kotlintoday.feature.mvvm.db.NoteDatabase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@HiltAndroidTest
@SmallTest
class NoteDaoTestWithDagger {

    @Inject
    @Named("test_db")
    lateinit var database: NoteDatabase

    lateinit var dao: NoteDao

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutor = InstantTaskExecutorRule()

    @Before
    fun setup() {
        hiltRule.inject()
        dao = database.noteDao()
    }

    @After
    fun close() {
        database.close()
    }

    @Test
    fun insertNote() = runBlocking {


    }

}