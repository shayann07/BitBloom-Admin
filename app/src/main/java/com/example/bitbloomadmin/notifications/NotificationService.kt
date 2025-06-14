package com.example.bitbloomadmin.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.bitbloomadmin.MainActivity
import com.example.bitbloomadmin.R
import com.example.bitbloomadmin.models.NotificationItem
import com.example.bitbloomadmin.utils.SharedPrefManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class NotificationService : FirebaseMessagingService() {
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var sharedPreferences: SharedPreferences
    private val channelId = "AssignId"

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = applicationContext.getSharedPreferences("MyPrefs", MODE_PRIVATE)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val id = sharedPreferences.getString("id", null)
        if (!id.isNullOrEmpty()) {
            firestore.collection("users").document(id).update("deviceToken", token)
        } else {
            Log.e("NotificationService", "User ID not found in SharedPreferences")
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("NotificationService", "onMessageReceived: ${message.data}")

        val title = message.data["title"] ?: "No Title"
        val body = message.data["body"] ?: "No Body"

        // Save to local notification list
        val newNotification = NotificationItem(title, body)
        val sharedPrefManager = SharedPrefManager(applicationContext)
        val currentList = sharedPrefManager.getNotifications().toMutableList()
        currentList.add(0, newNotification)
        sharedPrefManager.saveNotifications(currentList)

        // Create intent for notification click
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Assigned Work",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "New work notifications"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.announcement)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(Random.nextInt(), notification)
    }
}
