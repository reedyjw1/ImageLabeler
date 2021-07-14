package com.reedy.imagelabeler.model

import java.util.*

data class Annotation (
    var group: String? = null,
    var fileName: String? = null,
    var path: String? = null,
    var source: String = "Unspecified",
    var imageHeight: Int? = null,
    var imageWidth: Int? = null,
    var depth: Int = 3,
    var segmented: Int = 0,
    var pose: String = "Unspecified",
    var truncated: String = "Unspecified",
    var difficult: String = "Unspecified",
    var boxes: MutableList<Box> = mutableListOf()
)

data class MetaData(
    var uid: String? = UUID.randomUUID().toString(),
    var annotation: Annotation = Annotation(),
    var bitmapUri: String = "",
    var directoryUri: String = ""
)