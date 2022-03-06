package com.shihab.kotlintoday.feature.analytics

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.utility.LogMe
import kotlinx.android.synthetic.main.fragment_crash_analytics.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class CrashFragment : Fragment(R.layout.fragment_crash_analytics) {

    private var mNavController: NavController? = null
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            activity?.finish()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mNavController = Navigation.findNavController(view)
        activity?.onBackPressedDispatcher?.addCallback(this, onBackPressedCallback)

        button_crash_one.setOnClickListener {
            FirebaseCrashlytics.getInstance().log("A Force Error Occured")
        }

        button_crash_two.setOnClickListener {
            var value = null
            try {
                LogMe.e("crash:", value)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

        button_crash_three.setOnClickListener {

        }
    }
}
