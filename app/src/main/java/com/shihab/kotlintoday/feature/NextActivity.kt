package com.shihab.kotlintoday.feature

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.databinding.ActivityNextBinding


class NextActivity : AppCompatActivity() {
    lateinit var  binding: ActivityNextBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNextBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val value = getStringFromIntentOrShowError("key")

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, value, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getStringFromIntentOrShowError(intentKey: String): String {
        val text: String? = intent.getStringExtra(intentKey)
        if (text == null) {
            showDialog()
            return ""
        }
        return text
    }

    // I use anko to show a dialog, you can use your one.
    private fun showDialog() {
        Toast.makeText(this,"No value",Toast.LENGTH_LONG).show()
    }

}
