package com.shihab.notes.data.remote

import com.shihab.notes.data.model.Note
import retrofit2.http.GET

interface NotesApiService {

    @GET("getNotes")
    suspend fun getNotes(): List<Note>
}
