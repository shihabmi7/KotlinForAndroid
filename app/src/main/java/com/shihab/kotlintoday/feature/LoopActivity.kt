package com.shihab.kotlintoday.feature

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.databinding.ActivityLoopBinding


class LoopActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoopBinding
    var country = listOf<String>("Afganistan", "Bangladesh", "China", "Denmark")
    var items = listOf<Int>(2, 8, 20, 30)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoopBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)


        binding.content. btnLoop.setOnClickListener {

            var valueOfText = ""
            for (index in items.indices) {
                Log.d("For Loop", "Index $index and value: ${items[index]}")

                valueOfText += "\n" + "Index $index and value: ${items[index]}"
            }


            valueOfText += "\n This is another version of For Loop"

            for ((index, value) in country.withIndex()) {

                valueOfText += "\nindex : $index = $value"
            }


            valueOfText += "\n This is Range version of For Loop"

            for (i in 1..10 step 2) { //1 until  10 10 excluded

                valueOfText += "\nindex : $i"

            }

            binding.content.   txResultLoop.text = valueOfText
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
