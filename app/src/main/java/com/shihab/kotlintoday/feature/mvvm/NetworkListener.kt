package com.shihab.kotlintoday.feature.mvvm

interface NetworkListener {

    fun onSuccess(api_code: Int, string: String)
    fun onFailure(api_code: Int, string: String)
    fun onStart(api_code: Int, string: String)
}