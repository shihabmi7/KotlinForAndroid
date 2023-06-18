package com.shihab.kotlintoday.feature

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.shihab.kotlintoday.databinding.ActivityConditionBinding

class ConditionActivity : AppCompatActivity() {

    lateinit var binding: ActivityConditionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConditionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
    }

    fun checkCondition(age: Int): String {

        when (age) {

            10 -> return "Age is Ten"
            else -> {
                return "You are more than Ten"
            }

        }


    }

}
