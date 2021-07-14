package com.reedy.imagelabeler.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.reedy.imagelabeler.model.Annotation
import com.reedy.imagelabeler.model.Box
import com.reedy.imagelabeler.model.ImageData

class Converters {

    @TypeConverter
    fun toJson(annotation: Annotation) = Gson().toJson(annotation)

    @TypeConverter
    fun fromJson(value: String) = Gson().fromJson(value, Annotation::class.java)
}