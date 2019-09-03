package com.shihab.kotlintoday.feature

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.shihab.kotlintoday.R
import kotlinx.android.synthetic.main.activity_condition.*

class ConditionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_condition)
        setSupportActionBar(toolbar)


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
