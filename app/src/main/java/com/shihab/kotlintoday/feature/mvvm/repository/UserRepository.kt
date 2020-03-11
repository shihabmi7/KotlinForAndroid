package com.shihab.kotlintoday.feature.mvvm.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shihab.kotlintoday.rest.IMyAPI
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository {

    fun userLogIn(email: String, pass: String): LiveData<String> {


        val loginResponse = MutableLiveData<String>()

        IMyAPI().userLogin(email, pass).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                loginResponse.value = t.message

            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if (response.isSuccessful){
                    loginResponse.value = response.body()?.string()
                }  else{
                    loginResponse.value = response.body()?.string()
                }

            }

        })
        return loginResponse
    }
}