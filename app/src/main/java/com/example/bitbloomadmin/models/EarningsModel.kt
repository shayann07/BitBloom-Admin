package com.example.bitbloomadmin.models

data class EarningsModel(
    val buying_profit: Double=0.0,      // Profit earned daily
    val current_balance: Double=0.0,     // Profit made from purchases
    val daily_profit: Double=0.0,   // Referral-based profit
    val lifetime_referral_income: Double=0.0,      // Total earnings accumulated
    val lifetime_roi_income: Double=0.0,
    val lifetime_team_income: Double=0.0,
    val referral_profit: Double=0.0,
    val team_profit: Double=0.0,
    val total_earned: Double=0.0,
) // Profit from the user's referral team)
{
    fun toMap(): Map<String, Any> {
        return mapOf(
            "buying_profit" to buying_profit,
            "current_balance" to current_balance,
            "daily_profit" to daily_profit,
            "lifetime_referral_income" to lifetime_referral_income,
            "lifetime_roi_income" to lifetime_roi_income,
            "referral_profit" to referral_profit,
            "team_profit" to team_profit,
            "total_earned" to total_earned,
        )
    }
}