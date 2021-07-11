package com.reedy.imagelabeler.view.image

data class Box(
    var group: String? = null ,
    var label: String? = null,
    var width: Int? = null,
    var height: Int? = null,
    var xMin: Float? = null,
    var yMin: Float? = null,
    var xMax: Float? = null,
    var yMax: Float? = null,
    var relativeToBitmapXMax: Float? = null,
    var relativeToBitmapXMin: Float? = null,
    var relativeToBitmapYMax: Float? = null,
    var relativeToBitmapYMin: Float? = null,
)