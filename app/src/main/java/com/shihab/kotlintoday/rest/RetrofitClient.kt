package com.shihab.kotlintoday.rest

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private var instance: Retrofit? = null
    private var iMyAPI: IMyAPI? = null

    private val instancevalue: Retrofit
        get() {

            if (instance == null) {
                instance = Retrofit.Builder().baseUrl("https://jsonplaceholder.typicode.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
            }
            return instance!!
        }

    fun getAPIInterface(): IMyAPI {
        if (iMyAPI == null) {
            iMyAPI = instancevalue.create(IMyAPI::class.java)
        }
        return iMyAPI!!
    }

}