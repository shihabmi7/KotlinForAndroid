package com.shihab.kotlintoday

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_data_type.*
import kotlinx.android.synthetic.main.content_data_type.*

class DataTypeActivity : AppCompatActivity() {

    var age = 30
    val year: Int = 2000 // It can change ()

    val evenNumber = "Number is Even"
    val oddNumber = "Number is Odd"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_type)
        setSupportActionBar(toolbar)

        button_show.setOnClickListener {
            Toast.makeText(applicationContext, edit_name.text.toString(), Toast.LENGTH_LONG).show()

        }

        button_check_condition.setOnClickListener {


            var value = edit_odd_even.text

            if (value % 2 == 0) {
                Toast.makeText(applicationContext, evenNumber, Toast.LENGTH_LONG).show()

            } else {
                Toast.makeText(applicationContext, oddNumber, Toast.LENGTH_LONG).show()

            }

        }
    }

}
