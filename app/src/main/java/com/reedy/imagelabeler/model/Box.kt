package com.reedy.imagelabeler.model

import java.util.*

data class Box(
    var group: String? = null ,
    var label: String? = null,
    var labelAsInt: Int? = null,
    var fileName: String? = null,
    var path: String? = null,
    var source: String = "Unspecified",
    var uid: String? = UUID.randomUUID().toString(),
    var width: Int? = null,
    var height: Int? = null,
    var xMin: Float? = null,
    var yMin: Float? = null,
    var xMax: Float? = null,
    var yMax: Float? = null,
    var relativeToBitmapXMax: Int? = null,
    var relativeToBitmapXMin: Int? = null,
    var relativeToBitmapYMax: Int? = null,
    var relativeToBitmapYMin: Int? = null,
    var imageHeight: Int? = null,
    var imageWidth: Int? = null,
    var depth: Int = 3,
    var segmented: Int = 0,
    var pose: String = "Unspecified",
    var truncated: String = "Unspecified",
    var difficult: String = "Unspecified",
)