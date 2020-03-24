package com.shihab.kotlintoday.feature.dialog_fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.shihab.kotlintoday.R

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstToDialogFragment : Fragment(R.layout.fragment_first_to_dialog) {

    private var mNavController: NavController? = null
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            activity?.finish()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mNavController = Navigation.findNavController(view)

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            mNavController?.navigate(R.id.action_FirstFragment_to_DialogFragment)
        }

        activity?.onBackPressedDispatcher?.addCallback(this, onBackPressedCallback)
    }

    override fun onResume() {
        super.onResume()
        activity?.setTitle(R.string.title_activity_dialog_fragment_acivity)
    }

}
