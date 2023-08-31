package com.shihab.kotlintoday.feature.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.utility.LogMe

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        LogMe.e(this.javaClass.simpleName, "onNewToken called: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        LogMe.e(
            this.javaClass.simpleName, "onMessageReceived: " + remoteMessage.notification!!
                .body
        )
        showForegroundNotifications(remoteMessage)
    }

    /**
     * this function handles the data payload when the app is running on the background and foreground.
     *
     * @param intent - the data payload
     */
    override fun handleIntent(intent: Intent) {
        super.handleIntent(intent)
        Log.e("FirebaseIntent", intent.extras.toString())
        val bundle = intent.extras
        if (bundle != null) {
        }
    }

    /**
     * this function shows a notification bar with the app logo, title and subtitle.
     *
     * @param remoteMessage - the message received from fcm
     */
    private fun showForegroundNotifications(remoteMessage: RemoteMessage) {
        val mBuilder = NotificationCompat.Builder(this, getString(R.string.app_name))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(remoteMessage.notification!!.body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val notificationManager = NotificationManagerCompat.from(this)

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, mBuilder.build())
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.app_name)
            val description = getString(R.string.add_note)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(getString(R.string.app_name), name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}