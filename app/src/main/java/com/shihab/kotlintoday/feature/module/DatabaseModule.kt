package com.shihab.kotlintoday.feature.module

import android.content.Context
import androidx.room.Room
import com.shihab.kotlintoday.feature.mvvm.db.NoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): NoteDatabase {
        return Room.databaseBuilder(
            context, NoteDatabase::class.java, "note_db"
        ).build()
    }

}