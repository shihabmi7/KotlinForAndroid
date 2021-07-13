package com.shihab.kotlintoday.feature.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class KotlinTodayAnalyticsManager(private var firebaseAnalytics: FirebaseAnalytics?) {

    fun trackEvent(eventName: String, properties: Map<String, String>? = null) {
        val bundle = Bundle()
        if (properties != null) {
            for ((key, value) in properties) {
                bundle.putString(key, value)
            }
        }
        firebaseAnalytics?.logEvent(eventName, bundle)
    }

    class Builder {

        private var firebaseAnalytics: FirebaseAnalytics? = null

        fun setFirebaseAnalytics(firebaseAnalytics: FirebaseAnalytics): Builder {
            this.firebaseAnalytics = firebaseAnalytics
            return this
        }

        fun build(): KotlinTodayAnalyticsManager {
            return KotlinTodayAnalyticsManager(firebaseAnalytics)
        }
    }
}