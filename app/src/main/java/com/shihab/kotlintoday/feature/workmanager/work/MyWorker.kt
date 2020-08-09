package com.shihab.kotlintoday.feature.workmanager.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.shihab.kotlintoday.R

class MyWorker(val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    companion object {
        val KEY_INPUT_DATA: String = "KEY_DATA"
        val KEY_OUTPUT_DATA: String = "KEY_DATA"
    }

    private val CHANNELID: String = "12345"

    override fun doWork(): Result {
        val data = inputData.getString(KEY_INPUT_DATA)
        displayNotification("Task Title", data!!)

        val outputData = Data.Builder().putString(KEY_OUTPUT_DATA,"I finish my job!!!").build()
        return Result.success(outputData)
    }

    fun displayNotification(task: CharSequence, desc: CharSequence) {
        val manager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNELID,
                "KotlinToday",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, CHANNELID)
        builder.setContentTitle(task).setContentText(desc).setSmallIcon(R.mipmap.ic_launcher)
        manager.notify(1, builder.build())
    }
}