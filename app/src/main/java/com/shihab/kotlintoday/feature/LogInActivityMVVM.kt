package com.shihab.kotlintoday.feature

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.databinding.ActivityLogInMvvmBinding
import com.shihab.kotlintoday.feature.mvvm.LogInViewModel
import com.shihab.kotlintoday.feature.mvvm.NetworkListener
import com.shihab.kotlintoday.utility.ShowToast


class LogInActivityMVVM : AppCompatActivity(), NetworkListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_log_in_mvvm)

        val bind: ActivityLogInMvvmBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_log_in_mvvm)

        var viewmodel = ViewModelProviders.of(this).get(LogInViewModel::class.java)

        bind.contentLogIn.viewmodel = viewmodel

        viewmodel.listener = this


    }

    override fun onSuccess(api_code: Int, logInRespone: LiveData<String>) {

        logInRespone.observe(this, Observer {
            ShowToast(it)
        })

    }

    override fun onFailure(api_code: Int, string: String) {

        ShowToast(string)
    }

    override fun onStart(api_code: Int, string: String) {

        ShowToast(string)
    }


}
