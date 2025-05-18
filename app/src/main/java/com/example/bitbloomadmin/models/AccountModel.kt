package com.example.bitbloomadmin.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Entity(tableName = "account_table")
data class AccountModel(
    @PrimaryKey val userId: String,
    val totalDeposit: Double = 0.0,
    val totalWithdraw: Double = 0.0,
    val remainingBalance: Double = 0.0,
    val updatedAt: Timestamp = Timestamp.now()
)
