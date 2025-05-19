package com.example.bitbloomadmin.models
data class UserWithAccount(
    val name: String = "",
    val userId: String = "",
    val email: String = "",
    val password: String = "",
    val referalCode: String = "",
    val phone: String = "",
    val accountId: String = "",
    val totalDeposit: Double = 0.0,
    val currentBalance: Double = 0.0,
    val withdraw: Double,
    val totalEarned: Double = 0.0,
    val lifetime_referral_income: Double = 0.0,
    val lifetime_roi_income: Double = 0.0,
    val lifetime_team_income: Double = 0.0
)
