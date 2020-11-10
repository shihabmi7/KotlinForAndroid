package com.shihab.kotlintoday.feature

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.utility.AppUtils
import kotlinx.android.synthetic.main.activity_data_type.*
import kotlinx.android.synthetic.main.content_data_type.*

class DataTypeWithCalculatorActivity : AppCompatActivity() {

    var age = 30
    val year: Int = 2000 // It can change ()

    val evenNumber = "Number is Even"
    val oddNumber = "Number is Odd"
    val greetings = "Hello!"

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

            var value = edit_odd_even.text.toString().toInt()

            if (value % 2 == 0) {

                edit_odd_even.setText("")
                Toast.makeText(applicationContext, evenNumber, Toast.LENGTH_LONG).show()

            } else {
                edit_odd_even.setText("")
                Toast.makeText(applicationContext, oddNumber, Toast.LENGTH_LONG).show()
            }

        }

        button_add.setOnClickListener {

            if (!edit_first_number.text.isEmpty()) {
                tx_result.setText(
                    add(
                        edit_first_number.text.toString().toInt(),
                        edit_second_number.text.toString().toInt()
                    ).toString()
                )
            } else {


                Toast.makeText(applicationContext, "Give value", Toast.LENGTH_LONG).show()
            }

            AppUtils.hideKeyboard(this)
        }


        button_subtract.setOnClickListener {

            tx_result.setText(
                substract(
                    edit_first_number.text.toString().toInt(),
                    edit_second_number.text.toString().toInt()
                ).toString()
            )
            AppUtils.hideKeyboard(this)

        }

        button_multiply.setOnClickListener {

            tx_result.setText(
                multiple(
                    edit_first_number.text.toString().toInt(),
                    edit_second_number.text.toString().toInt()
                ).toString()
            )
            AppUtils.hideKeyboard(this)

        }

        button_divide.setOnClickListener {

            tx_result.setText(
                divide(
                    edit_first_number.text.toString().toFloat(),
                    edit_second_number.text.toString().toFloat()
                ).toString()
            )

            AppUtils.hideKeyboard(this)

        }

        button_clear.setOnClickListener {

            edit_first_number.setText("")
            edit_second_number.setText("")
            tx_result.setText("")


        }


    }

    fun add(a: Int, b: Int): Int {

        return a + b

    }

    fun substract(a: Int, b: Int): Int {

        return a - b

    }

    fun multiple(a: Int, b: Int): Int {

        return a * b

    }

    fun divide(a: Float, b: Float): Float {

        if (a != 0f) {

            return a / b
        } else
            return 0f
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item!!.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
