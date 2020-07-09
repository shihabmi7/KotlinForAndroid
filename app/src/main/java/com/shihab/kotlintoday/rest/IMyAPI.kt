package com.shihab.kotlintoday.rest

import com.shihab.kotlintoday.feature.mvvm.model.Note
import com.shihab.kotlintoday.model.Post
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface IMyAPI {
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
}