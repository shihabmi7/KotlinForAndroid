package com.shihab.kotlintoday.utility

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.facebook.stetho.Stetho
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.firebase.analytics.FirebaseAnalytics
import com.shihab.kotlintoday.feature.analytics.KotlinTodayAnalyticsManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KotlinToday : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        manager = SplitInstallManagerFactory.create(this)
        SplitCompat.install(this)
        Stetho.initializeWithDefaults(this)
    }

    companion object {
        private var analyticsManager: KotlinTodayAnalyticsManager? = null
        private var firebaseAnalytics: FirebaseAnalytics? = null
        private lateinit var manager: SplitInstallManager

        fun getSplitInstallManager(): SplitInstallManager {
            return manager
        }

        private fun getFirebaseAnalytics(context: Context): FirebaseAnalytics {
            if (firebaseAnalytics != null)
                return firebaseAnalytics!!
            firebaseAnalytics = FirebaseAnalytics.getInstance(context)
            return firebaseAnalytics!!
        }

        fun getB2BAnalyticsManager(context: Context): KotlinTodayAnalyticsManager {
            if (analyticsManager != null)
                return analyticsManager!!
            analyticsManager = KotlinTodayAnalyticsManager.Builder()
                .setFirebaseAnalytics(getFirebaseAnalytics(context)).build()
            return analyticsManager!!
        }
    }
}