package com.eugene.notificationstest

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.Toast
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MyJobService : JobService() {

    private val executorService : Executor = Executors.newSingleThreadExecutor()
    private lateinit var channelId: String
    private lateinit var mNotification: Notification
    private var id = 0

    override fun onStartJob(params: JobParameters?): Boolean {
        //JobService will run on the main thread. That means that you need to manage any asynchronous tasks yourself

        executorService.execute {

            setNotificationChannel()
            setNotification()
            notifyMe()
            jobFinished(params, false)
        }

        // Return true as there's more work to be done with this job.
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

    private fun setNotificationChannel() {

        if (Build.VERSION.SDK_INT >= 26) {
            channelId = "activities_channel_id"
            val channelName = "Activities"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        } else {
            channelId = "default"
        }
    }

    private fun setNotification() {

        val mBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("My notification")
                .setContentText("Hello World!")
                .setPriority(Notification.PRIORITY_MAX)
                .setAutoCancel(true)

        val notifyIntent = Intent(this, SecondActivity::class.java)
        // Sets the Activity to start in a new, empty task
        notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        // Creates the PendingIntent
        val notifyPendingIntent = PendingIntent.getActivity(
                this,
                0,
                notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        // Puts the PendingIntent into the notification builder
        mBuilder.setContentIntent(notifyPendingIntent)

        mNotification = mBuilder.build()
    }

    private fun notifyMe() {
        if(this::mNotification.isInitialized) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(id++, mNotification)
        } else
            Toast.makeText(this, "Notification is not ready", Toast.LENGTH_SHORT).show()
    }

}
