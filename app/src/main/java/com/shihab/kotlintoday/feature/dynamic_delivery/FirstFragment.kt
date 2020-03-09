package com.shihab.kotlintoday.feature.dynamic_delivery

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.play.core.splitinstall.SplitInstallException
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallErrorCode
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.google.android.play.core.tasks.OnFailureListener
import com.google.android.play.core.tasks.OnSuccessListener
import com.shihab.kotlintoday.BuildConfig
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.utility.KotlinToday
import com.shihab.kotlintoday.utility.LogMe
import kotlin.math.roundToInt


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

            //loadFeatureOne()
            loadModule()

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

    fun launchActivity() {
        val packageName = "com.shihab.feature_one"
        val activityClassName = "$packageName.FeatureOneHome"

        val intent = Intent(Intent.ACTION_VIEW).setClassName(
            BuildConfig.APPLICATION_ID, // BuildConfig of app Module
            activityClassName
        )
        startActivity(intent)
    }


    fun loadModule() {

        val moduleName = "feature_one"
        val splitInstallManager = SplitInstallManagerFactory.create(context)
        if (!splitInstallManager.installedModules.contains(moduleName)) {
            LogMe.d("", "Install module: $moduleName")
            val request =
                SplitInstallRequest
                    .newBuilder()
                    // You can download multiple on demand modules per
                    // request by invoking the following method for each
                    // module you want to install.
                    .addModule(moduleName)
                    // .addModule("promotionalFilters")
                    .build()

            // TODO: move listener code and activeSessionId to ViewModel
            var activeSessionId: Int? = null
            var listener: SplitInstallStateUpdatedListener? = null
            listener = SplitInstallStateUpdatedListener { state ->
                if (state.sessionId() == activeSessionId) {
                    when (state.status()) {
                        SplitInstallSessionStatus.DOWNLOADING -> {

                            val percentage =
                                (state.bytesDownloaded() / state.totalBytesToDownload().toFloat() * 100).roundToInt()
                            LogMe.d("", "Downloading $percentage%")

                        }
                        SplitInstallSessionStatus.INSTALLED -> {
                            launchActivity()
                            splitInstallManager.unregisterListener(listener)
                        }
                        SplitInstallSessionStatus.FAILED -> {
                            splitInstallManager.unregisterListener(listener)
                        }
                        else -> {

                        }
                    }
                }
            }
            splitInstallManager.registerListener(listener)

            // TODO: need to show some UI loading screen
            splitInstallManager
                // Submits the request to install the module through the
                // asynchronous startInstall() task. Your app needs to be
                // in the foreground to submit the request.
                .startInstall(request)
                // You should also be able to gracefully handle
                // request state changes and errors. To learn more, go to
                // the section about how to Monitor the request state.
                .addOnSuccessListener { sessionId ->
                    // should not launch activity here, as success doesn't mean the module is installed
                    // listen to SplitInstallStateUpdatedListener event instead
                    // launchActivity("SAMPLE_ID")
                    activeSessionId = sessionId
                }
                .addOnFailureListener { exception ->
                    when ((exception as SplitInstallException).errorCode) {
                        SplitInstallErrorCode.NETWORK_ERROR -> {
                            LogMe.e("Network error", "Network error")

                        }
                        else -> {
                            LogMe.e("splitmanager", "splitInstallManager failed")

                        }
                    }
                }
        } else {
            LogMe.e("Module found", "" + moduleName)
            launchActivity()
        }
    }

}
