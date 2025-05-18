package com.example.bitbloomadmin.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
@Entity(tableName = "user_table")
data class UserModel(
    @PrimaryKey val userId: String,
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val referralCode: String = "",
    val status: String = "",
    val createdAt: Timestamp = Timestamp.now()
)


