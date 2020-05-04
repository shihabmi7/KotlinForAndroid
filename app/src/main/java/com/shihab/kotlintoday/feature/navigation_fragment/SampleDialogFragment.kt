package com.shihab.kotlintoday.feature.navigation_fragment

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.shihab.kotlintoday.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SampleDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SampleDialogFragment : DialogFragment(), View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mNavController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onWarningAlertDialog(activity!!, "Alert", "Do you want to close this application ?")
            //mNavController?.navigateUp()

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sample_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "This is Dialog Fragment"

        mNavController = Navigation.findNavController(view)
        activity?.onBackPressedDispatcher?.addCallback(this, onBackPressedCallback)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SampleDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SampleDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun onWarningAlertDialog(value: Activity, title: String, message: String) {

        val dialogBuilder = AlertDialog.Builder(value)
        dialogBuilder.setMessage(/*""*/message)
            .setCancelable(false)
            .setTitle(title)
            .setPositiveButton("Proceed", DialogInterface.OnClickListener { dialog, id ->

                //activity?.supportFragmentManager?.popBackStack()
                mNavController?.navigateUp()

            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("AlertDialogExample")
        // show alert dialog
        alert.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
        }
    }
}
