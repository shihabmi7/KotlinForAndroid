package com.shihab.kotlintoday.feature.dynamic_delivery

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.play.core.splitinstall.*
import com.google.android.play.core.splitinstall.model.SplitInstallErrorCode
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.google.android.play.core.tasks.OnFailureListener
import com.google.android.play.core.tasks.OnSuccessListener
import com.shihab.kotlintoday.BuildConfig
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.utility.KotlinToday
import com.shihab.kotlintoday.utility.LogMe
import kotlinx.android.synthetic.main.fragment_first.*
import kotlin.math.roundToInt


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(R.layout.fragment_first) {

    val moduleNameOne = "feature_one"
    val moduleNameTwo = "feature_two"
    var activeSessionId: Int? = null
    var splitInstallManager: SplitInstallManager? = null
    var listener: SplitInstallStateUpdatedListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            //loadFeatureOne()
            loadModule()
        }

        button_second.setOnClickListener {

        }
    }

    fun launchActivity() {
        val packageName = "com.shihab.feature_one"
        val activityClassName = "$packageName.FeatureOneHome"

        try {
            val intent = Intent(Intent.ACTION_VIEW).setClassName(
                BuildConfig.APPLICATION_ID, // BuildConfig of app Module
                activityClassName
            )
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "" + e.printStackTrace(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadModule() {

        splitInstallManager = SplitInstallManagerFactory.create(activity)

        try {
            if (!splitInstallManager!!.installedModules.contains(moduleNameOne)) {
                LogMe.d("", "Install module: $moduleNameOne")
                val request =
                    SplitInstallRequest
                        .newBuilder()
                        // You can download multiple on demand modules per
                        // request by invoking the following method for each
                        // module you want to install.
                        .addModule(moduleNameOne)
                        .addModule(moduleNameTwo)
                        .build()

                // TODO: move listener code and activeSessionId to ViewModel

                listener = SplitInstallStateUpdatedListener { state ->
                    try {
                        if (state.sessionId() == activeSessionId) {
                            when (state.status()) {
                                SplitInstallSessionStatus.DOWNLOADING -> {

                                    val percentage =
                                        (state.bytesDownloaded() / state.totalBytesToDownload()
                                            .toFloat() * 100).roundToInt()
                                    LogMe.d("", "Downloading $percentage%")

                                }
                                SplitInstallSessionStatus.INSTALLED -> {
                                    launchActivity()
                                    splitInstallManager!!.unregisterListener(listener)
                                }
                                SplitInstallSessionStatus.FAILED -> {
                                    splitInstallManager!!.unregisterListener(listener)
                                }
                                else -> {
                                    Toast.makeText(
                                        context,
                                        "Error" + state.status(),
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                splitInstallManager!!.registerListener(listener)

                // TODO: need to show some UI loading screen
                splitInstallManager!!
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
                LogMe.e("Module found", "" + moduleNameOne)
                launchActivity()
            }
        } catch (e: Exception) {
            e.printStackTrace()
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