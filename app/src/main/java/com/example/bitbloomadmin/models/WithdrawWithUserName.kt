package com.example.bitbloomadmin.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bitbloom.bitbloomadmin.utils.TimestampConverter
import com.bitbloom.bitbloomadmin.utils.WithdrawModelConverter

@Entity(tableName = "withdraws_with_user")
@TypeConverters(TimestampConverter::class)
data class WithdrawWithUserName(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val withdraw: WithdrawModel,
    val userName: String = ""
)
