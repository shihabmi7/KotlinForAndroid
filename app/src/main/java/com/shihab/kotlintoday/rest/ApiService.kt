package com.shihab.kotlintoday.rest

import com.shihab.kotlintoday.feature.mvvm.model.Note
import com.shihab.kotlintoday.feature.paging.RickAndMortyList
import com.shihab.kotlintoday.model.Post
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface ApiService {
    @get:GET("posts")
    val getPost: Observable<List<Post>>

    @FormUrlEncoded
    @POST("login")
    fun userLogin(
        @Field("email") email: String,
        @Field("password") pass: String
    ): Call<ResponseBody>

    @GET("https://9cd57f79-7273-4069-8c74-d648a82453d9.mock.pstmn.io/getNotes")
    suspend fun getNotes(): List<Note>

    @GET("https://9cd57f79-7273-4069-8c74-d648a82453d9.mock.pstmn.io/getNotes")
    fun getNotesRaw(): List<Note>

    @GET("https://rickandmortyapi.com/api/character")
    suspend fun getDataFromAPI(@Query("page") query: Int): RickAndMortyList
}