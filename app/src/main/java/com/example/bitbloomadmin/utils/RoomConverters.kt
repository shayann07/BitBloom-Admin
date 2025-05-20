package com.example.bitbloomadmin.utils

import androidx.room.TypeConverter
import java.util.Date

object RoomConverters {
    @TypeConverter
    @JvmStatic
    fun fromMillis(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    @JvmStatic
    fun dateToMillis(date: Date?): Long? = date?.time
}