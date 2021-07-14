package com.reedy.imagelabeler.model

import java.util.*

data class ImageData(
    var uid: String? = UUID.randomUUID().toString(),
    var annotation: Annotation = Annotation(),
    var bitmapUri: String = "",
    var directoryUri: String = ""
)

data class Annotation (
    var fileName: String = "",
    var path: String = "",
    var group: String = "",
    var source: String = "Unspecified",
    var imageHeight: Int = 0,
    var imageWidth: Int = 0,
    var depth: Int = 3,
    var segmented: Int = 0,
    var pose: String = "Unspecified",
    var truncated: String = "Unspecified",
    var difficult: String = "Unspecified",
    var boxes: MutableList<Box> = mutableListOf()
)

data class Box(
    var uid: String = UUID.randomUUID().toString(),
    var width: Int = 0,
    var height: Int = 0,
    var xMin: Float = 0f,
    var yMin: Float = 0f,
    var xMax: Float = 0f,
    var yMax: Float = 0f,
    var label: String = "",
    var group: String = "",
    var xScale: Float = 0f,
    var yScale: Float = 0f
)