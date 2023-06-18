package com.shihab.kotlintoday.feature

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.databinding.ActivityImagviewBinding
import com.shihab.kotlintoday.model.loadImage

class ImageViewActivity : AppCompatActivity() {

    lateinit var bind: ActivityImagviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = DataBindingUtil.setContentView(this, R.layout.activity_imagview)
        //setContentView(R.layout.activity_imagview)
        setSupportActionBar(bind.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        bind.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        bind.contentImgview.imageview.loadImage("http://i.imgur.com/DvpvklR.png")

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item?.itemId == android.R.id.home){
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
