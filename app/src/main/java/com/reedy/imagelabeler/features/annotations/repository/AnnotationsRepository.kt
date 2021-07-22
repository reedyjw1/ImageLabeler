package com.reedy.imagelabeler.features.annotations.repository

import android.content.Context
import android.util.Log
import com.reedy.imagelabeler.db.AppDatabase
import com.reedy.imagelabeler.model.ImageData

class AnnotationsRepository(context: Context): IAnnotationsRepository {

    private val appDatabase = AppDatabase.getInstance(context).imageDataDao()

    override suspend fun loadAllById(id: String): List<ImageData> {
        return appDatabase.loadAllById(id)
    }

    override suspend fun loadByImageUri(id: String): ImageData? {
        return appDatabase.loadDataFromImageUri(id)
    }

    override suspend fun loadAllByProjectId(id: String): List<ImageData> {
        return appDatabase.loadAllByProjectId(id)
    }

    override fun saveAnnotations(imageData: ImageData) {
        Log.i("Repository", "saveSelectedAnnotation: $imageData")
        appDatabase.saveAnnotations(imageData)
    }
}