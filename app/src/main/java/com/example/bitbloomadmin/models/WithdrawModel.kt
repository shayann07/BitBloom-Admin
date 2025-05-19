package com.example.bitbloomadmin.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
@Entity(tableName = "withdrawals")
data class WithdrawModel(
    @PrimaryKey val transactionId: String = "",
    val userId: String = "",
    val amount: Double = 0.0,
    val address: String = "",
    val balanceUpdated: Boolean = false,
    val status: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val type: String = ""
)
