package com.norbertotaveras.reels.model.local.converter

import androidx.room.TypeConverter

class ReelsTypeConverters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(separator = "|")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return value
            .takeIf { it.isNotBlank() }
            ?.split("|")
            ?: emptyList()
    }
}
