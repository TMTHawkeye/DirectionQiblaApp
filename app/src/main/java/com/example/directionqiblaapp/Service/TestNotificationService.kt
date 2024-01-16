package com.example.directionqiblaapp.Service


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.directionqiblaapp.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class TestNotificationService : Service() {

    companion object {
        const val TAG = "TestNotificationService"
        const val CHANNEL_ID = "TestNotificationChannel"
        const val NOTIFICATION_ID = 2
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "TestNotificationService started")

        // Show a notification with the current time
        showNotification()

        // The service will be explicitly stopped when the work is done
//        stopSelf()

        return START_NOT_STICKY
    }

    private fun showNotification() {
        // Create a notification channel (required for Android Oreo and above)
        createNotificationChannel()

        // Build the notification
        val currentTime = getCurrentTime()
        val notificationText = "Current time: $currentTime"

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Test Notification")
            .setContentText(notificationText)
            .setSmallIcon(R.drawable.notification_icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Show the notification
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Test Notification Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return dateFormat.format(Date())
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
