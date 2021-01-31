package com.shihab.kotlintoday.feature

import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.shihab.kotlintoday.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class HomeActivityTest {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(HomeActivity::class.java)
    private val fetchingIdlingResource = FetchingIdlingResource()

    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(fetchingIdlingResource)
    }

    @Test
    fun test_isActivityInView() {

        onView(withId(R.id.layoutContent)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // perform a click
        //(withId(R.id.refresh)).perform(click())

        /*val resultCode = scenario.result.resultCode
        val resultData = scenario.result.resultData*/

        /** To call method activity itself*/
        activityScenarioRule.scenario.onActivity { activity ->
            activity.onButtonClick(0)
        }

        /** Move to a activity life cycle*/
        activityScenarioRule.scenario.moveToState(Lifecycle.State.CREATED)

        /** back button press*/
        pressBack()
    }

    @Test
    fun test_checkRecyclerViewItem() {
        onView(withId(R.id.recycler_content)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

}