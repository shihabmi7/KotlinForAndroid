package com.shihab.kotlintoday.feature.mvvm.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.shihab.kotlintoday.feature.mvvm.db.NoteDatabase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@SmallTest
class NoteDaoTestWithDagger {

    private lateinit var database: NoteDatabase
    private lateinit var dao: NoteDao

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    var instantTaskExecutor = InstantTaskExecutorRule()

    @Before
    fun setup() {

    }

    @After
    fun close() {

    }

    @Test
    fun insertNote() = runBlocking {


    }

}