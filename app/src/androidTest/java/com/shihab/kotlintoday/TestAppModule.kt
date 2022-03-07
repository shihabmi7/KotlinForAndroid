package com.shihab.kotlintoday

import android.content.Context
import androidx.room.Room
import com.shihab.kotlintoday.feature.mvvm.db.NoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @Named("test_db")
    fun provideInMemoryInDB(@ApplicationContext context: Context) =
        Room.inMemoryDatabaseBuilder(context, NoteDatabase::class.java).allowMainThreadQueries()
            .build()
}