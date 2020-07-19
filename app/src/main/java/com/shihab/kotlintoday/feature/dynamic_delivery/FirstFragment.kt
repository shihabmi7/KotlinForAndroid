package com.shihab.kotlintoday.feature.dynamic_delivery

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.play.core.splitinstall.*
import com.google.android.play.core.splitinstall.model.SplitInstallErrorCode
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.shihab.kotlintoday.BuildConfig
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.utility.LogMe
import kotlinx.android.synthetic.main.fragment_first.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(R.layout.fragment_first) {

    val moduleNameOne = "feature_one"
    val moduleNameTwo = "feature_two"
    val moduleNameThree = "FeatureThree"
    var requestedFeatureName = ""
    var activeSessionId: Int? = null
    var splitInstallManager: SplitInstallManager? = null
    var listener: SplitInstallStateUpdatedListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_first.setOnClickListener {
            loadModule(moduleNameOne)
        }

        button_second.setOnClickListener {
            loadModule(moduleNameTwo)
        }
    }

    private fun launchFeatureOneActivity() {
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

    private fun launchFeatureTwoActivity() {
        val packageName = "com.shihab.feature_two"
        val activityClassName = "$packageName.FeatureTwoActivity"

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

    private fun getSplitInstallRequest(moduleName: String): SplitInstallRequest {
        return SplitInstallRequest
            .newBuilder()
            // You can download multiple on demand modules per
            // request by invoking the following method for each
            // module you want to install.
            .addModule(moduleName)//.addModule(anotherOne)
            .build()
    }

    private fun loadModule(moduleName: String) {

        requestedFeatureName = moduleName
        splitInstallManager = SplitInstallManagerFactory.create(activity)

        try {
            if (!splitInstallManager!!.installedModules.contains(moduleNameOne)) {
                LogMe.d("", "Install module: $moduleNameOne")

                // TODO: move listener code and activeSessionId to ViewModel

                listener = SplitInstallStateUpdatedListener { state ->
                    try {
                        if (state.sessionId() == activeSessionId) {
                            when (state.status()) {
                                SplitInstallSessionStatus.DOWNLOADING -> {
                                    val percentage =
                                        (state.bytesDownloaded() / state.totalBytesToDownload()
                                            .toFloat() * 100).toInt()
                                    LogMe.d("", "Downloading $percentage%")
                                }
                                SplitInstallSessionStatus.INSTALLED -> {
                                    if (requestedFeatureName.contentEquals(moduleNameOne)) {
                                        launchFeatureOneActivity()
                                    } else if (requestedFeatureName.contentEquals(moduleNameTwo)) {
                                        launchFeatureTwoActivity()
                                    }
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
                    .startInstall(getSplitInstallRequest(moduleName))
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
                                LogMe.e(
                                    "splitmanager",
                                    "splitInstallManager failed :->" + exception.localizedMessage
                                )
                            }
                        }
                    }
            } else {
                LogMe.e("Module found", "" + moduleNameOne)
                launchFeatureOneActivity()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}