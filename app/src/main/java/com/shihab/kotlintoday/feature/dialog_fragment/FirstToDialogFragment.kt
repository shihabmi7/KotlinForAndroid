package com.shihab.kotlintoday.feature.dialog_fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.shihab.kotlintoday.R

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstToDialogFragment : Fragment(R.layout.fragment_first_to_dialog) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_DialogFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.setTitle(R.string.title_activity_dialog_fragment_acivity)
    }
}