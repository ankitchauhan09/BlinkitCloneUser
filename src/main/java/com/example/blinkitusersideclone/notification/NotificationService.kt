package com.example.blinkitusersideclone.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.blinkitusersideclone.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class NotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val channelId = "UserBlinkit"
        val channel = NotificationChannel(
            channelId,
            "Blinkit",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "Blinkit Messages"
        channel.enableLights(true)

        val manager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        // Safely access notification data
        val notificationTitle = message.data["notification"] ?: "No Title"
        val notificationBody = message.data["body"] ?: "No Body"

        val notification = NotificationCompat.Builder(this@NotificationService, channelId)
            .setContentTitle(notificationTitle)
            .setContentText(notificationBody)
            .setSmallIcon(R.drawable.app_icon)
            .build()

        manager.notify(Random.nextInt(), notification)
    }

}