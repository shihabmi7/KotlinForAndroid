package com.shihab.kotlintoday.feature.viewBinding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.shihab.kotlintoday.databinding.ActivityViewBindingBinding
import kotlinx.android.synthetic.main.activity_view_binding.*
import kotlinx.android.synthetic.main.content_view_binding.*

class ViewBindingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityViewBindingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        textview_test.text = "Hello world in view Binding...!!!"
        setSupportActionBar(toolbar)
        supportActionBar?.title = "view Binding"
    }
}
