package com.reedy.imagelabeler.view.overlay.model

data class Box(
    var group: String? = null ,
    var label: String? = null,
    var width: Int? = null,
    var height: Int? = null,
    var xMin: Float? = null,
    var yMin: Float? = null,
    var xMax: Float? = null,
    var yMax: Float? = null,
    var scale: Float? = null,
    var absoluteXMin: Float? = null,
    var absoluteXMax: Float? = null,
    var absoluteYMin: Float? = null,
    var absoluteYMax: Float? = null,
)