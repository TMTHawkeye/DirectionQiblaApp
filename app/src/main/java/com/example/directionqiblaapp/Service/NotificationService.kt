package com.example.directionqiblaapp.Service

// Create a new Kotlin file for the service, e.g., NotificationService.kt

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.directionqiblaapp.Fragments.PrayerNotification
import com.example.directionqiblaapp.R
import io.paperdb.Paper
import java.util.concurrent.TimeUnit

class NotificationService : Service() {

    companion object {
        const val CHANNEL_ID = "PrayerTimeChannel"
        const val NOTIFICATION_ID = 1
        const val EXTRA_PRAYER_TEXT = "extra_prayer_text"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val prayerText = intent?.getStringExtra(EXTRA_PRAYER_TEXT) ?: ""
        val notificationMethod = getSelectedNotificationMethod() // Retrieve user's selected notification method

        scheduleNotification(prayerText,notificationMethod)

//        stopSelf()

        return START_NOT_STICKY
    }

    private fun getSelectedNotificationMethod(): String? {
        // Retrieve the user's selected notification method from SharedPreferences or any other storage
        // For example, you can use Paper library to store/retrieve data
        return Paper.book().read("selectedNotificationMethod", "Beep") // Default to "Beep" if not set
    }

    private fun playBeepNotification() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.beep)
        mediaPlayer.start()
    }
    private fun playMuteNotification() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.mute)
        mediaPlayer.start()
    }

    private fun vibrateNotification() {
        // Implement logic to vibrate the device
        // For example, you can use the Vibrator service
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(200)
        }
    }


    private fun playTakbeerNotification() {
        // Implement logic to play Takbeer sound
        // For example, you can use MediaPlayer to play the Takbeer audio file
        val mediaPlayer = MediaPlayer.create(this, R.raw.takbir)
        mediaPlayer.start()
    }

    private fun playFullAdhanNotification() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.full_adhaan)
        mediaPlayer.start()
    }



    private fun scheduleNotification(comingPrayer: String,notificationMethod: String?) {
        createNotificationChannel()

        when (notificationMethod) {
            "Beep" -> playBeepNotification()
            "Mute" -> playMuteNotification()
            "Vibrate" -> vibrateNotification()
            "Takbeer" -> playTakbeerNotification()
            "Full Adhan" -> playFullAdhanNotification()
            else -> Log.d("NotificationService", "Unknown notification method: $notificationMethod")
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Next Prayer Time")
            .setContentText(comingPrayer)
            .setSmallIcon(R.drawable.notification_icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Prayer Time Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
