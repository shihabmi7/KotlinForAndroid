package com.shihab.kotlintoday.feature

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.shihab.kotlintoday.R
import kotlinx.android.synthetic.main.activity_first.*
import kotlinx.android.synthetic.main.content_first.*

class FirstActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)
        setSupportActionBar(toolbar)
        button_click.setOnClickListener {

            startActivity(Intent(this, NextActivity::class.java).putExtra("key","i came from First Activity"))
        }


    }

}
