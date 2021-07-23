package com.reedy.imagelabeler.utils

data class CocoJson(
    val info: InfoCoco,
    val licensesCoco: List<LicensesCoco>,
    val imagesCoco: List<ImagesCoco>,
    val categoriesCoco: List<CategoriesCoco>,
    val annotationsCoco: List<AnnotationsCoco>
)

data class InfoCoco(
    val description: String,
    val url: String,
    val version: String,
    val year: Int,
    val contributor: String,
    val date_created: String
)

data class LicensesCoco(
    val url: String,
    val id: Int,
    val name: String
)

data class ImagesCoco(
    val license: Int,
    val file_name: String,
    val height: Int,
    val width: Int,
    val date_captured: String,
    val flickr_url: String,
    val id: Int
)

data class CategoriesCoco(
    val supercategory: String,
    val id: Int,
    val name: String
)

data class AnnotationsCoco(
    val segmentation: List<List<Float>>,
    val area: Double,
    val isCrowd: Int,
    val image_id: Int,
    val category_id: Int,
    val id: Int
)