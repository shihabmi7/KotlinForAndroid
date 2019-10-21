package com.shihab.kotlintoday.feature

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.feature.coroutine.CoroutineActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_home.*

class HomeActivity : AppCompatActivity(), View.OnClickListener,
    ActivityNameAdapter.OnButtonClickListener {


    var activiites = listOf(
        FirstActivity::class.java,
        DataTypeWithCalculatorActivity::class.java,
        LoopActivity::class.java,
        RecyclerViewWithRetrofitRx::class.java,
        ImplicitIntentActivity::class.java,
        LogInActivityMVVM::class.java,
        CoroutineActivity::class.java
    )
    var activiites_name = listOf(
        "Activity Switchting",
        "DataTypeWithCalculatorActivity",
        "Looping Activity",
        "RecyclerViewWithRetrofitRx",
        "Implicit Intent ", "MVVM", "Co-routine"
    )

    override fun onButtonClick(position: Int) {

        val i = Intent(this, activiites[position])
        startActivity(i)
    }

    override fun onClick(v: View?) {

    }

    /*Class classes[] = [ firstActivity.class, DetailActivity.class];
    Intent i = new Intent(this, classes[position]);
    startActivity(i);
    */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)

        recycler_content.layoutManager = LinearLayoutManager(this)
        recycler_content.adapter =
            ActivityNameAdapter(
                applicationContext,
                activiites_name,
                this
            )

        recycler_content.setHasFixedSize(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
