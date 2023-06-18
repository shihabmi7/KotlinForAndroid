package com.shihab.kotlintoday.feature

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.utility.AppUtils
import com.shihab.kotlintoday.databinding.ActivityDataTypeBinding

class DataTypeWithCalculatorActivity : AppCompatActivity() {

    lateinit var binding: ActivityDataTypeBinding
    var age = 30
    val year: Int = 2000 // It can change ()

    val evenNumber = "Number is Even"
    val oddNumber = "Number is Odd"
    val greetings = "Hello!"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.content.buttonShow.setOnClickListener {
            binding.content.editOddEven.setText("")
            Toast.makeText(
                applicationContext,
                greetings + " " + binding.content.editName.text.toString(),
                Toast.LENGTH_LONG
            ).show()


        }

        binding.content.buttonCheckCondition.setOnClickListener {

            var value = binding.content.editOddEven.text.toString().toInt()

            if (value % 2 == 0) {

                binding.content.editOddEven.setText("")
                Toast.makeText(applicationContext, evenNumber, Toast.LENGTH_LONG).show()

            } else {
                binding.content.editOddEven.setText("")
                Toast.makeText(applicationContext, oddNumber, Toast.LENGTH_LONG).show()
            }

        }

        binding.content.buttonAdd.setOnClickListener {

            if (!binding.content. editFirstNumber.text.isEmpty()) {
                binding.content.  txResult.setText(
                    add(
                        binding.content. editFirstNumber.text.toString().toInt(),
                        binding.content. editSecondNumber.text.toString().toInt()
                    ).toString()
                )
            } else {


                Toast.makeText(applicationContext, "Give value", Toast.LENGTH_LONG).show()
            }

            AppUtils.hideKeyboard(this)
        }


        binding.content. buttonSubtract.setOnClickListener {

            binding.content .txResult.setText(
                substract(
                    binding.content. editFirstNumber.text.toString().toInt(),
                    binding.content. editSecondNumber.text.toString().toInt()
                ).toString()
            )
            AppUtils.hideKeyboard(this)

        }

        binding.content .buttonMultiply.setOnClickListener {

            binding.content .txResult.setText(
                multiple(
                    binding.content. editFirstNumber.text.toString().toInt(),
                    binding.content. editSecondNumber.text.toString().toInt()
                ).toString()
            )
            AppUtils.hideKeyboard(this)

        }

        binding.content. buttonDivide.setOnClickListener {

            binding.content .txResult.setText(
                divide(
                    binding.content. editFirstNumber.text.toString().toFloat(),
                    binding.content. editSecondNumber.text.toString().toFloat()
                ).toString()
            )

            AppUtils.hideKeyboard(this)

        }

        binding.content.buttonClear.setOnClickListener {

            binding.content. editFirstNumber.setText("")
            binding.content. editSecondNumber.setText("")
            binding.content. txResult.setText("")


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
