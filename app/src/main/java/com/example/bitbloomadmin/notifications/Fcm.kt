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
        type: String,
        accessToken: String
    ) {
        val url = "https://fcm.googleapis.com/v1/projects/investment-app-11ac4/messages:send"
        val client = OkHttpClient()

        val messageJson = JSONObject().apply {
            put("token", targetDeviceToken)

            put("notification", JSONObject().apply {
                put("title", title)
                put("body", body)
                put("sound", type) // Will play res/raw/{type}.mp3 (e.g. profit, rejected, approved)
            })

            put("android", JSONObject().apply {
                put("priority", "HIGH")
                put("notification", JSONObject().apply {
                    put("sound", type) // Optional for Android-specific config
                })
            })

            put("data", JSONObject().apply {
                put("title", title)
                put("body", body)
                put("type", type)
            })
        }

        val wrapper = JSONObject().put("message", messageJson)
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = wrapper.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .post(requestBody)
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        val errorBody = response.body?.string()
                        Log.e(TAG, "HTTP ${response.code} – $errorBody")
                    } else {
                        Log.d(TAG, "Notification sent successfully")
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error sending notification", e)
            }
        }
    }

}
