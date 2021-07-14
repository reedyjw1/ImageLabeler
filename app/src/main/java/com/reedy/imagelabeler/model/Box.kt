package com.reedy.imagelabeler.model

import java.util.*

data class Box(
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
    var label: String? = null,
    var labelAsInt: Int? = null,)