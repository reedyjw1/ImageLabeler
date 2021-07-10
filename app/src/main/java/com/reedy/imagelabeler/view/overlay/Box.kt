package com.reedy.imagelabeler.view.overlay

data class Box(
    var group: String? = null ,
    var label: String? = null,
    var width: Int? = null,
    var height: Int? = null,
    var xMin: Int? = null,
    var yMin: Int? = null,
    var xMax: Int? = null,
    var yMax: Int? = null,
)