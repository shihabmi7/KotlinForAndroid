package com.shihab.kotlintoday.feature.user_interaction

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.databinding.ActivityUserInteractionBinding

class UserInteractionActivity : AppCompatActivity() {

    lateinit var binding: ActivityUserInteractionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityUserInteractionBinding>(
            this,
            R.layout.activity_user_interaction
        )

        binding.buttonShowToast.setOnClickListener {
            showToast()
        }

        binding.buttonShowCustomToast.setOnClickListener {
            customToast()
        }

        binding.buttonShowSnackBar.setOnClickListener {
            snackbar()
        }
    }

    fun customToast() {
        val layout = LayoutInflater.from(this).inflate(R.layout.layout_custom_toast, null)
        val tv = layout.findViewById<TextView>(R.id.textview_message)
        tv.text = "This is custom toast"
        val toast = Toast(this).run {
            duration = Toast.LENGTH_LONG
            view = layout
            setGravity(Gravity.BOTTOM, 0, 0)
            show()
        }
    }

    fun snackbar() {
        val snackbar = Snackbar.make(binding.constraintParent, "Snackbar", Snackbar.LENGTH_SHORT)
        snackbar.setAction("Done") {
            showToast()
        }
        snackbar.setActionTextColor(Color.RED)
        snackbar.show()
    }

    fun showNotification() {

    }

    fun showToast() {
        Toast.makeText(this, "Toast Message", Toast.LENGTH_SHORT).show()
    }
}