package com.example.bitbloomadmin.models

data class UserWithAccount(
    val userId: String = "",
    val name: String = "",
    val totalDeposit: Double = 0.0,
    val currentBalance: Double = 0.0,
    val withdraw: Double,
    val totalEarned: Double = 0.0
)