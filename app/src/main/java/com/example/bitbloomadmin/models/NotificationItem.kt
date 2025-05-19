package com.example.bitbloomadmin.models

import com.google.firebase.Timestamp

data class NotificationItem(
    val title: String = "",
    val body: String = "",
    val timestamp: Timestamp = Timestamp.now()
)