package com.bitbloom.bitbloomadmin.utils

import androidx.room.TypeConverter
import com.example.bitbloomadmin.models.EarningsModel
import com.example.bitbloomadmin.models.InvestmentModel
import com.google.firebase.Timestamp
import com.google.gson.Gson
import java.util.Date

class TimestampConverter {

    private val gson = Gson()
    @TypeConverter
    fun fromTimestamp(value: Timestamp?): Long? {
        return value?.seconds
    }

    @TypeConverter
    fun toTimestamp(seconds: Long?): Timestamp? {
        return seconds?.let { Timestamp(it, 0) }
    }
    @TypeConverter
    fun fromInvestment(value: InvestmentModel): String = gson.toJson(value)

    @TypeConverter
    fun toInvestment(value: String): InvestmentModel =
        gson.fromJson(value, InvestmentModel::class.java)

    @TypeConverter
    fun fromEarnings(value: EarningsModel): String = gson.toJson(value)

    @TypeConverter
    fun toEarnings(value: String): EarningsModel =
        gson.fromJson(value, EarningsModel::class.java)
}
