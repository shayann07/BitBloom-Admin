package com.example.bitbloomadmin.notifications

import android.util.Log
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class Fcm {
    companion object {
        private const val TAG = "Fcm"
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun sendFCMNotification(
        targetDeviceToken: String,
        title: String,
        body: String,

        accessToken: String
    ) {
        val url = "https://fcm.googleapis.com/v1/projects/investment-app-11ac4/messages:send"
        val client = OkHttpClient()

        // Simple notification with title and body
        val messageJson = JSONObject().apply {
            put("token", targetDeviceToken)

            put("notification", JSONObject().apply {
                put("title", title)
                put("body", body)
            })

            put("android", JSONObject().apply {
                put("priority", "HIGH")
            })

            put("data", JSONObject().apply {
                put("title", title)
                put("body", body)
            })
        }

        val json = JSONObject().put("message", messageJson)
        val requestBody = json.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                if (!response.isSuccessful) {
                    Log.e(TAG, "❌ FCM Error: ${response.code} – $responseBody")
                } else {
                    Log.d(TAG, "✅ Notification sent successfully")
                }
            } catch (e: IOException) {
                Log.e(TAG, "❌ Error sending notification: ${e.message}")
            }
        }
    }
}
