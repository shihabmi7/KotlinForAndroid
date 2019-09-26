package com.shihab.kotlintoday.feature

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.shihab.kotlintoday.R
import kotlinx.android.synthetic.main.activity_implicit_intent.*
import kotlinx.android.synthetic.main.content_implicit_intent.*

class ImplicitIntentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_implicit_intent)
        setSupportActionBar(toolbar)

        /*fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/


        share_button.setOnClickListener {

            var message = editText_share.text.toString()
            var title = "Implicit intent"
            var intent = Intent()

            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, message)
            intent.type = "text/plain"

            startActivity( Intent.createChooser(intent,title))
        }

    }

}
