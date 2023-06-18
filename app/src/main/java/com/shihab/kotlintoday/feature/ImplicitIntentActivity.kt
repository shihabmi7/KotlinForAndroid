package com.shihab.kotlintoday.feature

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.databinding.ActivityImplicitIntentBinding

class ImplicitIntentActivity : AppCompatActivity() {
lateinit var binding: ActivityImplicitIntentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityImplicitIntentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        /*fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/


        binding.content.shareButton .setOnClickListener {

            val message = binding.content.editTextShare.text.toString()
            val title = "Implicit intent"
            var intent = Intent()

            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, message)
            intent.type = "text/plain"

            startActivity( Intent.createChooser(intent,title))
        }

    }

}
