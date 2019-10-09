package com.shihab.kotlintoday.feature

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.databinding.ActivityLogInMvvmBinding
import com.shihab.kotlintoday.feature.mvvm.LogInViewModel
import com.shihab.kotlintoday.feature.mvvm.NetworkListener


class LogInActivityMVVM : AppCompatActivity(), NetworkListener {
    override fun onSuccess(api_code: Int, string: String) {

    }

    override fun onFailure(api_code: Int, string: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStart(api_code: Int, string: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setContentView(R.layout.activity_log_in_mvvm)

        val bind: ActivityLogInMvvmBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_log_in_mvvm)

        var viewmodel = ViewModelProviders.of(this).get(LogInViewModel::class.java)

        bind.contentLogIn.viewmodel = viewmodel

        viewmodel.listener = this

//        setSupportActionBar(toolbar)

    }


}
