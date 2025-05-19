package com.example.bitbloomadmin.models

data class InvestmentModel(
    val total_deposit: Double=0.0,     // Total amount deposited by the user
    val stake_profit: Double=0.0, // The remaining balance in the account
    val remaining_balance: Double=0.0,
)     // The profit earned from staking
{
    fun toMap(): Map<String, Any> {
        return mapOf(
            "totalDeposit" to total_deposit,
            "stake_profit" to stake_profit,
            "remaining_balance" to remaining_balance
        )
    }
}