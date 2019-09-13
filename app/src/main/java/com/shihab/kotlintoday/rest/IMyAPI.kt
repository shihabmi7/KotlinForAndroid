package com.shihab.kotlintoday.rest

import com.shihab.kotlintoday.model.Post
import io.reactivex.Observable
import retrofit2.http.GET


interface IMyAPI {
    @get:GET("posts")
    val post: Observable<List<Post>>
}