package com.reedy.imagelabeler.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class ImageData(
    @PrimaryKey
    var uid: String = UUID.randomUUID().toString(),
    @ColumnInfo
    var annotation: Annotation = Annotation(),
    @ColumnInfo
    var bitmapUri: String = "",
    @ColumnInfo
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

fun MutableList<Box>.checkAndSwap(): MutableList<Box> {
    return  map {
        val box = Box()
        box.uid = it.uid
        box.width = it.width
        box.height = it.height
        box.xMin = it.xMin
        box.xMax = it.xMax
        box.yMax = it.yMax
        box.yMin = it.yMin
        box.label = it.label
        box.group = it.group
        box.xScale = it.xScale
        box.yScale = it.yScale

        if (it.xMin > it.xMax) {
            box.xMax = it.xMin
            box.xMin = it.xMax
        }
        if (it.yMin > it.yMax) {
            box.yMax = it.yMin
            box.yMin = it.yMax
        }
        return@map box
    }.toMutableList()
}