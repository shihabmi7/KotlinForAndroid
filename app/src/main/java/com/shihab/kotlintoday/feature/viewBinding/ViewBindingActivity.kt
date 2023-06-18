package com.shihab.kotlintoday.feature.viewBinding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.shihab.kotlintoday.databinding.ActivityViewBindingBinding

class ViewBindingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityViewBindingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //binding.text = "Hello world in view Binding...!!!"

        setSupportActionBar(binding.toolbar)
        binding.contentViewBinding.textviewTest.text = "View Binding with Jetpack Compose"
        supportActionBar?.title = "view Binding"
    }
}
