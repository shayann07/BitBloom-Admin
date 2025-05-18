package com.example.bitbloomadmin.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Entity(tableName = "withdrawals")
data class WithdrawModel(
    @PrimaryKey
    val id: String,
    val address: String = "",
    val amount: Double = 0.0,
    val balanceUpdated: Boolean = false,
    val status: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val transactionId: String = "",
    val type: String = "",
    val userId: String = ""
)