package com.shihab.kotlintoday.feature.mvvm

import android.view.View
import androidx.lifecycle.ViewModel

class LogInViewModel : ViewModel() {

    var email: String? = null
    var password: String? = null
    var listener: NetworkListener? = null

    val API_LOG_IN = 200

    fun onLogInButtion(view: View) {

        listener?.onStart(API_LOG_IN, "on Started")

        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            listener?.onFailure(API_LOG_IN, "EMAIL IS EMPTY AND PASS IS NOT RIGHT")
            return
        }else{
            val lologInResponse = UserRepository().userLogIn(email!!, password!!)
             listener?.onSuccess(API_LOG_IN,lologInResponse)

        }

    }

}