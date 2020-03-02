package com.shihab.feature_one

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class FeatureOneHome : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feature_one_home)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
