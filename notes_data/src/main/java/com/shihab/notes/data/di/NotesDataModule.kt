package com.shihab.notes.data.di

import android.content.Context
import androidx.room.Room
import com.shihab.notes.data.dao.NoteDao
import com.shihab.notes.data.db.NoteDatabase
import com.shihab.notes.data.remote.NotesApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

private const val NOTES_BASE_URL = "https://9cd57f79-7273-4069-8c74-d648a82453d9.mock.pstmn.io/"

// Hilt merges every @InstallIn(SingletonComponent) module from every Gradle
// module into one component graph. :app's own AppModule already provides an
// unqualified Retrofit/OkHttpClient for the rest of the app's networking, so
// providing another unqualified Retrofit/OkHttpClient here would be a
// [Dagger/DuplicateBindings] compile error the moment :app depends on this
// module. This qualifier scopes the notes-only network stack to its own
// bindings instead of colliding with :app's.
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NotesNetwork

@Module
@InstallIn(SingletonComponent::class)
object NotesDataModule {

    // No qualifier needed here: NoteDatabase/NoteDao are types unique to this
    // module, so there's nothing elsewhere in the app to collide with.
    @Singleton
    @Provides
    fun provideNoteDatabase(@ApplicationContext context: Context): NoteDatabase =
        Room.databaseBuilder(context, NoteDatabase::class.java, "note_db").build()

    @Provides
    fun provideNoteDao(database: NoteDatabase): NoteDao = database.noteDao()

    @NotesNetwork
    @Singleton
    @Provides
    fun provideNotesOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

    @NotesNetwork
    @Singleton
    @Provides
    fun provideNotesRetrofit(@NotesNetwork okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(NOTES_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Singleton
    @Provides
    fun provideNotesApiService(@NotesNetwork retrofit: Retrofit): NotesApiService =
        retrofit.create(NotesApiService::class.java)
}
