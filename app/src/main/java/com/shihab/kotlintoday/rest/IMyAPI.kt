package com.shihab.kotlintoday.rest

import com.shihab.kotlintoday.model.Post
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST


interface IMyAPI {
    @get:GET("posts")
    val post: Observable<List<Post>>

    @GET("posts")
    fun getPosts(): Observable<List<Post>>

    @FormUrlEncoded
    @POST("login")
    fun userLogin(
        @Field("email") email: String,
        @Field("password") pass: String
    ): Call<ResponseBody>

    companion object {
        var BASE_URL = "base_url"

        operator fun invoke(): IMyAPI {
            return Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())

                .build().create(IMyAPI::class.java)
        }
    }
}