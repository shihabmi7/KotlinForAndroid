package com.shihab.kotlintoday.feature

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.utility.LogMe
import kotlinx.android.synthetic.main.activity_first.*
import kotlinx.android.synthetic.main.content_first.*

class FirstActivity : AppCompatActivity() {

    val TAG  = "LIFECYCLE"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)
        setSupportActionBar(toolbar)
        button_click.setOnClickListener {

            startActivity(Intent(this, NextActivity::class.java).putExtra("key","i came from First Activity"))
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        LogMe.i(TAG, "onCreate called")

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        LogMe.i(TAG, "onStart called")
    }

    override fun onResume() {
        super.onResume()
        LogMe.i(TAG, "onResume called")
    }

    override fun onPause() {
        super.onPause()
        LogMe.i(TAG, "onPause called")
    }

    override fun onStop() {
        super.onStop()
        LogMe.i(TAG, "onStop called")
    }

    override fun onRestart() {
        super.onRestart()
        // called when home button presssed onrestart > onStart > onResume
        LogMe.i(TAG, "onRestart called")
    }

    override fun onDestroy() {
        super.onDestroy()
        LogMe.i(TAG, "onDestroy called")
    }

}
