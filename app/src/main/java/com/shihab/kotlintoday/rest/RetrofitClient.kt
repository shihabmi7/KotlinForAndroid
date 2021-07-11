package com.shihab.kotlintoday.rest

import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private var instance: Retrofit? = null
    private var iMyAPI: IMyAPI? = null

    private val instancevalue: Retrofit
        get() {

            if (instance == null) {
                instance = Retrofit.Builder().baseUrl("https://jsonplaceholder.typicode.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
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

    private fun okHttpClient(): OkHttpClient? {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor()) // used if network off OR on
            .addNetworkInterceptor(StethoInterceptor())
            .build()
    }

    private fun httpLoggingInterceptor(): HttpLoggingInterceptor {
//        val httpLoggingInterceptor =
//            HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
//                LogMe.d("Retrofit", "log: http log: $message")
//            })

        val httpLoggingInterceptor = HttpLoggingInterceptor()

        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

}