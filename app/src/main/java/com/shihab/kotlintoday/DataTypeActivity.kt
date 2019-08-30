package com.shihab.kotlintoday

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_data_type.*
import kotlinx.android.synthetic.main.content_data_type.*

class DataTypeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_type)
        setSupportActionBar(toolbar)

        var toast = Toast.makeText(applicationContext, "", Toast.LENGTH_LONG)

        button_show.setOnClickListener {
            Toast.makeText(applicationContext, edit_name.text.toString(), Toast.LENGTH_LONG).show()
        }
    }

}
