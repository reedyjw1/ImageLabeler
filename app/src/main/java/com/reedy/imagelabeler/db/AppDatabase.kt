package com.reedy.imagelabeler.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.reedy.imagelabeler.model.ImageData
import com.reedy.imagelabeler.model.Project

@Database(entities = [ImageData::class, Project::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {

    companion object {
        fun getInstance(context: Context): AppDatabase = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "annotations.db").build()
    }

    abstract fun imageDataDao(): ImageDataDao
    abstract fun projectDao(): ProjectDao
}
