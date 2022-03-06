package com.shihab.kotlintoday.feature.parcelable

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.databinding.ActivityReceiveParcelableAcitivtyBinding

class ReceiveParcelableAcitivty : AppCompatActivity() {

    private val PERSON = "person"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bind = ActivityReceiveParcelableAcitivtyBinding.inflate(layoutInflater)
        setContentView(bind.root)

        intent?.let {
            val person:Person = intent.extras?.getParcelable<Person>(PERSON) as Person
            bind.textViewData.text = " Data Receive: $person"
        }

        title = "Receive Parcelable Data"

    }
}
