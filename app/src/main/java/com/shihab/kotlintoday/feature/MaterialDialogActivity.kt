package com.shihab.kotlintoday.feature

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.snackbar.Snackbar
import com.shihab.kotlintoday.R
import  com.shihab.kotlintoday.databinding.ActivityMaterialDialogBinding

class MaterialDialogActivity : AppCompatActivity() {

    lateinit var binding: ActivityMaterialDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaterialDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }


        setupDemo()
    }

    private fun setupDemo() {


        binding.content.successToast.setOnClickListener {

            displayToast("success_toast")

        }

        binding.content.errorToast.setOnClickListener {

            displayToast("error tost")
        }

        binding.content.successDialog.setOnClickListener {

            displaySuccessfulDialog("successful")


        }

        binding.content.errorDialog.setOnClickListener {

            displayErrorfulDialog("Error")
        }

        binding.content. areYouSure.setOnClickListener {

            val areYouSureCallBack = object : AreYouSureCallBack{
                override fun proceed() {

                    displayToast("successfully did this!")
                }

                override fun cancel() {

                    displayToast("you cancelled this action")

                }
            }

            areYouSureDialog("Are you sure to perform this action",areYouSureCallBack)
        }

    }


    fun displayToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    fun displaySuccessfulDialog(message: String?) {

        MaterialDialog(this).show {

            title(R.string.text_success)
            message(text = message)
            positiveButton(R.string.text_ok)
        }
    }

    fun displayErrorfulDialog(message: String?) {

        MaterialDialog(this).show {

            title(R.string.text_error)
            message(text = message)
            negativeButton(R.string.text_ok)
        }
    }

    fun areYouSureDialog(message: String, callBack: AreYouSureCallBack) {

        MaterialDialog(this).show {

            title(R.string.text_error)
            message(text = message)
            negativeButton(R.string.text_cancel) {
                callBack.cancel()
            }
            positiveButton {
                callBack.proceed()
            }
        }
    }

    interface AreYouSureCallBack {

        fun proceed()
        fun cancel()
    }


}
