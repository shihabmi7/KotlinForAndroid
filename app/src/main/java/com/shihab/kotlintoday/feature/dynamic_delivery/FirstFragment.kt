package com.shihab.kotlintoday.feature.dynamic_delivery

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.tasks.OnFailureListener
import com.google.android.play.core.tasks.OnSuccessListener
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.utility.KotlinToday


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_first).setOnClickListener {

            loadFeatureOne()

        }
    }

    fun loadFeatureOne() { // Builds a request to install the feature1 module

        val request = SplitInstallRequest
            .newBuilder() // You can download multiple on demand modules per
            // request by invoking the following method for each
            // module you want to install.
            .addModule("feature_one")
            .build()

        // Begin the installation of the feature1 module and handle success/failure
        KotlinToday.getSplitInstallManager()
            .startInstall(request)
            .addOnSuccessListener(OnSuccessListener<Int?> {
                // Module download successful
                Toast.makeText(context, "Module download successful", Toast.LENGTH_LONG).show()
                //context?.startActivity(Intent(context, com.shihab.feature_one.::class.java))

                startActivity(
                    Intent()
                        .setClassName(context, "com.shihab.feature_one.FeatureOneHome")
                )
            })
            .addOnFailureListener(OnFailureListener {
                // Module download failed; handle the error here
                Toast.makeText(
                    context,
                    "Module download failed; handle the error here",
                    Toast.LENGTH_LONG
                ).show()

            })
    }

}
