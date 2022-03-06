package com.shihab.kotlintoday.feature.broadcast

import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.utility.AppUtils.EXPLICIT_BROADCAST_ACTION

class BroadCastActivity : AppCompatActivity() {

    lateinit var kotlinTodayReceiver: KotlinTodayReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_broad_cast)
        kotlinTodayReceiver = KotlinTodayReceiver()

        findViewById<Button>(R.id.btnExplicitReceiver).setOnClickListener {
            triggerExplicitReceiver()
        }

    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(kotlinTodayReceiver, filter)
    }

    private fun triggerExplicitReceiver() {
        val intent = Intent()
        intent.action = EXPLICIT_BROADCAST_ACTION
        intent.component = ComponentName(
            packageName,
            "com.shihab.kotlintoday.feature.broadcast.KotlinTodayReceiver"
        )
        applicationContext.sendBroadcast(intent)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(kotlinTodayReceiver)
    }
}