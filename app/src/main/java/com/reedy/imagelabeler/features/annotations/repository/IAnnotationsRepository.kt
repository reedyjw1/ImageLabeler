package com.reedy.imagelabeler.features.annotations.repository
import com.reedy.imagelabeler.model.ImageData

interface IAnnotationsRepository {
    suspend fun loadAllById(id: String): List<ImageData>
    suspend fun loadByImageUri(id: String): ImageData?
    fun saveAnnotations(imageData: ImageData)
}