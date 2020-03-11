package com.shihab.kotlintoday.rest.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class ErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.code == 401) {
            throw SessionExpiredException("" + response.code + " Error Interceptor")
        }
        return response
    }
}

//https://medium.com/@azfarsiddiqui/why-use-network-interceptors-6927e448c90b
