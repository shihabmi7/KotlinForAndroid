package com.shihab.kotlintoday.feature.analytics

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.utility.LogMe
import com.shihab.kotlintoday.databinding.ActivityCrashAnalyticsBinding

class CrashAnalyticsActivity : AppCompatActivity() {
lateinit var binding: ActivityCrashAnalyticsBinding
    var tag = "CrashAnalytics"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCrashAnalyticsBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_crash_analytics)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            super.onBackPressed();
            LogMe.i(tag, "home clicked.")
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
