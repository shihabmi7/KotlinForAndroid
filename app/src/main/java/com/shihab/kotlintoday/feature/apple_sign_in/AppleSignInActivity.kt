package com.shihab.kotlintoday.feature.apple_sign_in

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.databinding.ActivityAppleSignInBinding

class AppleSignInActivity : AppCompatActivity() {

    lateinit var binding : ActivityAppleSignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_apple_sign_in)
        setSupportActionBar(binding.toolbar)

        binding.content.relativeLayout.setOnClickListener {
            Toast.makeText(applicationContext, "clicked", Toast.LENGTH_SHORT).show()
        }

    }
}