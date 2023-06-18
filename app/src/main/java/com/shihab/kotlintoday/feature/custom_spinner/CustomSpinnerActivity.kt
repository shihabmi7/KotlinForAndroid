package com.shihab.kotlintoday.feature.custom_spinner

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.databinding.ActivityCustomSpinnerBinding

class CustomSpinnerActivity : AppCompatActivity() {
    lateinit var binding: ActivityCustomSpinnerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCustomSpinnerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.spinner.adapter = MoodAdapter(
            this,
            listOf(
                ReqDocsItem("Angry"),
                ReqDocsItem("Happy"),
                ReqDocsItem("Playful"),
                ReqDocsItem("Wondering")
            )
        )

        binding. spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val clickedItem: ReqDocsItem = parent!!.getItemAtPosition(position) as ReqDocsItem
                val professionName: String = clickedItem.name!!
                Toast.makeText(
                    this@CustomSpinnerActivity,
                    "$professionName selected",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}