package com.shihab.kotlintoday.feature

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.shihab.kotlintoday.R
import kotlinx.android.synthetic.main.activity_next.*

class NextActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_next)
        setSupportActionBar(toolbar)

        //  this is unsafe
        //var value = intent.getStringExtra("key")

        var value = getStringFromIntentOrShowError("key")



        fab.setOnClickListener { view ->
            Snackbar.make(view, value, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item!!.itemId == android.R.id.home) {
            onBackPressed()
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

        Toast.makeText(applicationContext,"No value",Toast.LENGTH_LONG).show()
    }

}
