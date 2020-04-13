package com.shihab.kotlintoday.feature.navigation_fragment

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.utility.LogMe
import kotlinx.android.synthetic.main.activity_dialog_fragment_acivity.*

class DialogFragmentWithNavigationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog_fragment_acivity)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            //NavUtils.navigateUpFromSameTask(this);
            if (supportFragmentManager.backStackEntryCount > 1) {
                supportFragmentManager.popBackStackImmediate()
                LogMe.i("DIALOGfRAG","backStackEntryCount.")
            } else {
                super.onBackPressed();
                LogMe.i("DIALOGfRAG","CLICKED.")
                //finish()

            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}