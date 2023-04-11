package com.shihab.kotlintoday.feature.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.shihab.kotlintoday.utility.AppUtils.EXPLICIT_BROADCAST_ACTION

class KotlinTodayReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == EXPLICIT_BROADCAST_ACTION) {
            Toast.makeText(context, "Explicit Broadcast was triggered", Toast.LENGTH_SHORT).show()
        }

        if ("android.net.conn.CONNECTIVITY_CHANGE" == action) {
            Toast.makeText(
                context,
                "Implicit Broadcast was triggered using registerReceiver",
                Toast.LENGTH_SHORT
            ).show()
        }

    }
}