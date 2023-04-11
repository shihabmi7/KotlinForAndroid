package com.shihab.kotlintoday.feature.broadcast

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.utility.ConnectionLiveData

class InternetConnectivityActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_internet_connectivity)
        val textView = findViewById<TextView>(R.id.textView)
        ConnectionLiveData(this).observe(this) {
            textView.text = if (it) "connected" else "disconnected"
            Log.d("AppLog", "connected?$it")
        }
        val internetSettings = findViewById<View>(R.id.internetSettings)
        internetSettings.isVisible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        internetSettings.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                startActivity(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY))
        }
        findViewById<View>(R.id.wifiSettings).setOnClickListener {
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
        }
        findViewById<View>(R.id.mobileDataSettings).setOnClickListener {
            startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
        }
    }
}