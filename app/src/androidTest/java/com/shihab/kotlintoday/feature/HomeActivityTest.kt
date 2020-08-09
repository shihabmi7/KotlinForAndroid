package com.shihab.kotlintoday.feature

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.shihab.kotlintoday.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class HomeActivityTest {

    @get:Rule
    val ActivityScenario = ActivityScenarioRule(HomeActivity::class.java)
    @Test
    fun test_isActivityInView() {

//        val activityScene = ActivityScenario.launch(HomeActivity::class.java)
        onView(ViewMatchers.withId(R.id.layoutContent)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }

}