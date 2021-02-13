package com.shihab.kotlintoday.feature

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.feature.Calculator.add
import com.shihab.kotlintoday.feature.Calculator.divide
import com.shihab.kotlintoday.feature.Calculator.multiple
import com.shihab.kotlintoday.feature.Calculator.substract
import com.shihab.kotlintoday.utility.AppUtils
import kotlinx.android.synthetic.main.activity_data_type.*
import kotlinx.android.synthetic.main.content_data_type.*

class DataTypeWithCalculatorActivity : AppCompatActivity() {

    private val evenNumber = "Number is Even"
    private val oddNumber = "Number is Odd"
    private val greetings = "Hello!"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_type)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        button_show.setOnClickListener {
            edit_odd_even.setText("")
            Toast.makeText(
                applicationContext,
                greetings + " " + edit_name.text.toString(),
                Toast.LENGTH_LONG
            ).show()
        }

        button_check_condition.setOnClickListener {

            val value = edit_odd_even.text.toString().toInt()

            if (value % 2 == 0) {
                edit_odd_even.setText("")
                Toast.makeText(applicationContext, evenNumber, Toast.LENGTH_LONG).show()

            } else {
                edit_odd_even.setText("")
                Toast.makeText(applicationContext, oddNumber, Toast.LENGTH_LONG).show()
            }

        }

        button_add.setOnClickListener {

            if (edit_first_number.text.isNotEmpty()) {
                tx_result.text = add(
                    edit_first_number.text.toString().toInt(),
                    edit_second_number.text.toString().toInt()
                ).toString()
            } else {
                Toast.makeText(applicationContext, "Give value", Toast.LENGTH_LONG).show()
            }

            AppUtils.hideKeyboard(this)
        }


        button_subtract.setOnClickListener {

            tx_result.text = substract(
                edit_first_number.text.toString().toInt(),
                edit_second_number.text.toString().toInt()
            ).toString()
            AppUtils.hideKeyboard(this)

        }

        button_multiply.setOnClickListener {

            tx_result.text = multiple(
                edit_first_number.text.toString().toInt(),
                edit_second_number.text.toString().toInt()
            ).toString()
            AppUtils.hideKeyboard(this)

        }

        button_divide.setOnClickListener {

            tx_result.text = divide(
                edit_first_number.text.toString().toFloat(),
                edit_second_number.text.toString().toFloat()
            ).toString()

            AppUtils.hideKeyboard(this)

        }

        button_clear.setOnClickListener {

            edit_first_number.setText("")
            edit_second_number.setText("")
            tx_result.text = ""


        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
