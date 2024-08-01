package com.tohed.islampro.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/*
class Converters {
    @TypeConverter
    fun fromList(value: List<Any>): String {
        val gson = Gson()
        val type = object : TypeToken<List<Any>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toList(value: String): List<Any> {
        val gson = Gson()
        val type = object : TypeToken<List<Any>>() {}.type
        return gson.fromJson(value, type)
    }
}
*/
