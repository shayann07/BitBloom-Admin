package com.example.bitbloomadmin.models

import com.google.firebase.Timestamp

data class AnnouncementModel(
    val id: String = "",
    val announcementTitlte: String = "",
    val message: String = "",
    val time: Timestamp = Timestamp.now()
)
