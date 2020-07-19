package com.shihab.kotlintoday.utility

import androidx.multidex.MultiDexApplication
import com.facebook.stetho.Stetho
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory

class KotlinToday : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        manager = SplitInstallManagerFactory.create(this)
        SplitCompat.install(this)
        Stetho.initializeWithDefaults(this);
    }

    companion object {

        private lateinit var manager: SplitInstallManager

        fun getSplitInstallManager(): SplitInstallManager {
            return manager
        }
    }

}