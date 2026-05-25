package com.norbertotaveras.reels.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ReelsTypeConverters {
    private val gson = Gson()
    private val stringListType = object : TypeToken<List<String>>() {}.type

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value, stringListType)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        if (value.isBlank()) return emptyList()
        if (!value.trimStart().startsWith("[")) return value.split("|")

        return gson.fromJson(value, stringListType)
    }
}
