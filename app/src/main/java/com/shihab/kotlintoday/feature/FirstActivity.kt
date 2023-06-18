package com.shihab.kotlintoday.feature

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.utility.LogMe
import  com.shihab.kotlintoday.databinding.ActivityFirstBinding;

class FirstActivity : AppCompatActivity() {
lateinit var  binding: ActivityFirstBinding
    val TAG  = "LIFECYCLE"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.content.buttonClick.setOnClickListener {

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
