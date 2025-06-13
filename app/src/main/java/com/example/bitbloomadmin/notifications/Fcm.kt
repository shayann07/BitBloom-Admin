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
        // Ensure this matches your service-account’s project ID
        val url = "https://fcm.googleapis.com/v1/projects/investment-app-11ac4/messages:send"
        val client = OkHttpClient()

        // Build a data-only message per HTTP v1 spec
        val messageJson = JSONObject().apply {
            put("token", targetDeviceToken)
            put("android", JSONObject().put("priority", "HIGH"))
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
                        Log.d(TAG, "Data-only notification sent successfully")
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error sending data-only notification", e)
            }
        }
    }
}
