package com.shihab.kotlintoday.feature.navigation_fragment

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.databinding.ActivityDialogFragmentAcivityBinding
import com.shihab.kotlintoday.databinding.ActivityDynamicDeliveryBinding
import com.shihab.kotlintoday.utility.LogMe


class DialogFragmentWithNavigationActivity : AppCompatActivity() {

    lateinit var binding: ActivityDialogFragmentAcivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDialogFragmentAcivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.fab.setOnClickListener { view ->
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