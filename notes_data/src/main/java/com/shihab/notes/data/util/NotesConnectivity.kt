package com.shihab.notes.data.util

import android.content.Context
import android.net.ConnectivityManager

object NotesConnectivity {

    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo
        return info != null && info.isConnected
    }
}
