package com.shihab.kotlintoday.feature.parcelable

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.databinding.ActivityParcelableBinding

class ParcelableActivity : AppCompatActivity() {

    private val PERSON = "person"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityParcelableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // first ensure a person object with data
        val person = Person("Shihab Uddin", 30, "shihab.mi7@gmail.com")

        binding.buttonSend.setOnClickListener {

            val intent = Intent(this, ReceiveParcelableAcitivty::class.java)
            //then put an parcelable extra to intent
            intent.putExtra(PERSON, person)
            startActivity(intent)

        }

        title = "Parcelable Test"
    }
}
