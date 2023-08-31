package com.shihab.kotlintoday.feature.map

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.shihab.kotlintoday.databinding.ActivityForegroundLocationUpdatesBinding

class ForegroundLocationUpdatesActivity : AppCompatActivity() {

    lateinit var binding: ActivityForegroundLocationUpdatesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForegroundLocationUpdatesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermission();

        binding.buttonStart.setOnClickListener {
            startService()
        }

        binding.buttonStop.setOnClickListener {

            stopService()
        }


    }

    fun requestPermission() {
      ActivityCompat.requestPermissions(this, arrayOf(
          android.Manifest.permission.ACCESS_COARSE_LOCATION,
          android.Manifest.permission.ACCESS_FINE_LOCATION,
      ), 1000)
    }

    fun startService() {
        Intent(this, LocationService.javaClass).apply {
            action = LocationService.ACTION_START
            startService(this)
        }
    }

    fun stopService() {
        Intent(this, LocationService.javaClass).apply {
            action = LocationService.ACTION_STOP
            startService(this)
        }
    }
}