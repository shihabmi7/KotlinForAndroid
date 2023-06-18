package com.shihab.kotlintoday.feature.coordinate_layout

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.databinding.ActivityCoordinateLayoutBinding


class CoordinateLayoutActivity : AppCompatActivity() {

    lateinit var binding: ActivityCoordinateLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoordinateLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

}
