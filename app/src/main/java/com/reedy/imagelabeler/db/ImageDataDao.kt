package com.reedy.imagelabeler.db

import androidx.room.*
import com.reedy.imagelabeler.model.ImageData

@Dao
interface ImageDataDao {

    @Query("SELECT * FROM imagedata WHERE uid == :id")
    fun loadAllById(id: String): List<ImageData>

    @Query("SELECT * FROM imagedata WHERE bitmapUri == :id")
    fun loadDataFromImageUri(id: String): ImageData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAnnotations(imageData: ImageData)
}