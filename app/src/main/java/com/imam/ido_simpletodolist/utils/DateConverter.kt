package com.imam.ido_simpletodolist.utils

import androidx.room.TypeConverter
import java.util.*

object DateConverter {
    @TypeConverter
    @JvmStatic
    fun toDate(timeStamp: Long?): Date? {
        return timeStamp?.let { Date(it) }
    }

    @TypeConverter
    @JvmStatic
    fun toTimeStamp(date: Date?): Long? {
        return date?.time
    }
}