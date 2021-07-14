package com.reedy.imagelabeler.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.reedy.imagelabeler.model.Box

class Converters {

    @TypeConverter
    fun listToJson(value: List<Box>?): String = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String): MutableList<Box> = Gson().fromJson(value, Array<Box>::class.java).toMutableList()

    @TypeConverter
    fun toJson(box: Box): String = Gson().toJson(box)

    @TypeConverter
    fun fromJson(value: String): Box = Gson().fromJson(value, Box::class.java)
}