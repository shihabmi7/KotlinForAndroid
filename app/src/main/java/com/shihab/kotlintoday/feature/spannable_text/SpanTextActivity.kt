package com.shihab.kotlintoday.feature.spannable_text

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.databinding.ActivitySpanTextBinding
import kotlinx.android.synthetic.main.activity_span_text.*

class SpanTextActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivitySpanTextBinding>(
            this,
            R.layout.activity_span_text
        )

        val string = SpannableString("Incorrect correct")
        string.setSpan(StrikethroughSpan(), 0, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv_span.text = string

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}