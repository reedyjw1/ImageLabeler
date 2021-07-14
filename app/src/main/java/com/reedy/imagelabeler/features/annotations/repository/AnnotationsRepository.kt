package com.reedy.imagelabeler.features.annotations.repository

import android.content.Context
import com.reedy.imagelabeler.db.AppDatabase
import com.reedy.imagelabeler.model.ImageData

class AnnotationsRepository(context: Context): IAnnotationsRepository {

    private val appDatabase = AppDatabase.getInstance(context).imageDataDao()

    override suspend fun loadAllById(id: String): List<ImageData> {
        return appDatabase.loadAllById(id)
    }

    override fun saveAnnotations(imageData: ImageData) {
        appDatabase.saveAnnotations(imageData)
    }
}