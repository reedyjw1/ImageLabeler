package com.reedy.imagelabeler.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(foreignKeys = [ForeignKey(entity = Project::class,
    parentColumns = arrayOf("projectUid"),
    childColumns = arrayOf("projectUid"),
    onDelete = ForeignKey.CASCADE)]
)
data class ImageData(
    @PrimaryKey
    var uid: String = UUID.randomUUID().toString(),
    @ColumnInfo
    var projectUid: String,
    @ColumnInfo
    var bitmapUri: String,
    @ColumnInfo
    var directoryUri: String = "",
    @ColumnInfo
    var fileName: String = "",
    @ColumnInfo
    var path: String = "",
    @ColumnInfo
    var group: String = "",
    @ColumnInfo
    var source: String = "Unspecified",
    @ColumnInfo
    var imageHeight: Int = 0,
    @ColumnInfo
    var imageWidth: Int = 0,
    @ColumnInfo
    var depth: Int = 3,
    @ColumnInfo
    var segmented: Int = 0,
    @ColumnInfo
    var pose: String = "Unspecified",
    @ColumnInfo
    var truncated: String = "Unspecified",
    @ColumnInfo
    var difficult: String = "Unspecified",
    @ColumnInfo
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

fun ImageData.removeAndUpdate(box: Box): ImageData {
    val imageData = this
    imageData.boxes.remove(box)
    return imageData
}

fun Box.checkAndSwap(): Box {

    if (xMin > xMax) {
        val tempXmax = xMax
        xMax = xMin
        xMin = tempXmax
    }
    if (yMin > yMax) {
        val tempYMax = yMax
        yMax = yMin
        yMin = tempYMax
    }

    return this
}