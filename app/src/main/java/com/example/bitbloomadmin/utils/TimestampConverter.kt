package com.bitbloom.bitbloomadmin.utils

import androidx.room.TypeConverter
import com.google.firebase.Timestamp
import java.util.Date

class TimestampConverter {
    @TypeConverter
    fun fromTimestamp(value: Timestamp?): Long? {
        return value?.seconds
    }

    @TypeConverter
    fun toTimestamp(seconds: Long?): Timestamp? {
        return seconds?.let { Timestamp(it, 0) }
    }
}
