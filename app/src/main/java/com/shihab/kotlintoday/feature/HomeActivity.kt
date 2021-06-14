package com.shihab.kotlintoday.feature

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.feature.apple_sign_in.AppleSignInActivity
import com.shihab.kotlintoday.feature.coordinate_layout.CoordinateLayoutActivity
import com.shihab.kotlintoday.feature.coroutine.CoroutineActivity
import com.shihab.kotlintoday.feature.crashanalytics.CrashAnalyticsActivity
import com.shihab.kotlintoday.feature.custom_spinner.CustomSpinnerActivity
import com.shihab.kotlintoday.feature.dynamic_delivery.DynamicDeliveryActivity
import com.shihab.kotlintoday.feature.mvvm.ui.NoteActivity
import com.shihab.kotlintoday.feature.navigation_fragment.DialogFragmentWithNavigationActivity
import com.shihab.kotlintoday.feature.parcelable.ParcelableActivity
import com.shihab.kotlintoday.feature.spannable_text.SpanTextActivity
import com.shihab.kotlintoday.feature.viewBinding.ViewBindingActivity
import com.shihab.kotlintoday.feature.workmanager.WorkManagerActivity
import com.shihab.kotlintoday.feature.motion_layout.MotionLayoutActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_home.*

class HomeActivity : AppCompatActivity(), View.OnClickListener,
    ActivityNameAdapter.OnButtonClickListener {

    var activiites = listOf(
        FirstActivity::class.java,
        DataTypeWithCalculatorActivity::class.java,
        LoopActivity::class.java,
        ClassActivity::class.java,
        CustomSpinnerActivity::class.java,
        RecyclerViewWithRetromfitRx::class.java,
        ImplicitIntentActivity::class.java,
        MaterialDialogActivity::class.java,
        NoteActivity::class.java,
        CoroutineActivity::class.java,
        ImageViewActivity::class.java,

        DynamicDeliveryActivity::class.java,
        ViewBindingActivity::class.java,
        ParcelableActivity::class.java,
        DialogFragmentWithNavigationActivity::class.java,
        CoordinateLayoutActivity::class.java,
        CrashAnalyticsActivity::class.java,
        WorkManagerActivity::class.java,
        SpanTextActivity::class.java,
        MotionLayoutActivity::class.java,
        AppleSignInActivity::class.java
    )

    var activiites_name = listOf(
        "Activity Switchting",
        "DataTypeWithCalculatorActivity",
        "Looping Activity",
        "Class In Kotlin",
        "Custom Spinner",
        "RecyclerViewWithRetrofitRx",
        "Implicit Intent ",
        "Material Dialog",
        "MVVM",
        "Co-routine",
        "Imageview Extension Function",
        "Dynamic Delivery: Feature One",
        "View Binding java", "Parcelable Activity", "Dialog Fragment With Navigation Graph",
        "Co-ordinate Layout", "Crash Analytics", "Work Manager", "Spannable Text",
        "Motion Layout",
        "Apple Sign In",
    )

    override fun onButtonClick(position: Int) {

        val i = Intent(this, activiites[position])
        startActivity(i)
    }

    override fun onClick(v: View?) {

    }

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
