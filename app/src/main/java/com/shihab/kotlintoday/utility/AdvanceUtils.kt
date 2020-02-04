package com.shihab.kotlintoday.utility

import android.app.Activity
import android.content.Context
import android.widget.Toast

fun Activity.showErrorMessage(context: Context, message: String) {

    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

fun Activity.showSuccessMessage(context: Context, message: String) {

    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}