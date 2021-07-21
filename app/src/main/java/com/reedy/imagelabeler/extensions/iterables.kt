package com.reedy.imagelabeler.extensions

import com.reedy.imagelabeler.features.annotations.model.UiDocument
import com.reedy.imagelabeler.features.annotations.model.UiLabel
import com.reedy.imagelabeler.model.Box
import com.reedy.imagelabeler.model.ImageData

fun <T> List<T>.replace(newValue: T, block: (T) -> Boolean): List<T> {
    return map {
        if (block(it)) newValue else it
    }
}

fun ImageData.addAndUpdate(newValue: Box): ImageData {
    boxes.add(newValue)
    return this
}

fun ImageData.resetBoxes(): ImageData {
    boxes = mutableListOf()
    return this
}

fun MutableList<UiLabel>.addAndUpdate(label: UiLabel): MutableList<UiLabel> {
    val newList = mutableListOf<UiLabel>()
    forEach {
        newList.add(it)
    }
    newList.add(label)
    return newList
}

fun MutableList<UiDocument>.updateSelected(document: UiDocument): MutableList<UiDocument> {
    return this.map {
        if (it.uri == document.uri && it.name == document.name) {
            UiDocument(it.name, it.uri, it.type, true)
        } else {
            UiDocument(it.name, it.uri, it.type, false)
        }
    }.toMutableList()
}

fun MutableList<UiLabel>.updateSelected(label: UiLabel): MutableList<UiLabel> {
    return this.map {
        if (it.uid == label.uid && it.name == label.name) {
            UiLabel(it.uid, it.name, it.group, it.labelNumber, true)
        } else {
            UiLabel(it.uid, it.name, it.group, it.labelNumber, false)
        }
    }.toMutableList()
}

fun MutableList<UiDocument>.findFirstImage(): UiDocument? {
    val extensions = arrayOf("jpg", "jpeg", "png", "bmp")
    forEach { doc ->
        extensions.forEach { ext ->
            if (doc.name.endsWith(ext)) {
                return doc
            }
        }
    }
    return null
}

fun MutableList<UiDocument>.findNext(doc: UiDocument): UiDocument? {
    this.forEachIndexed { index, uiDocument ->
        if (uiDocument.uri == doc.uri) {
            if (index != size-1) {
                return this[index+1]
            }
        }
    }
    return null
}

fun MutableList<UiDocument>.findPrevious(doc: UiDocument): UiDocument? {
    this.forEachIndexed { index, uiDocument ->
        if (uiDocument.uri == doc.uri) {
            if (index != 0) {
                return this[index-1]
            }
        }
    }
    return null
}

fun MutableList<UiDocument>.findSelected(): UiDocument? {
    this.forEach {
        if (it.selected) {
            return it
        }
    }
    return null
}


fun ImageData.copy(): ImageData {
    return ImageData(
        projectUid = projectUid,
        uid = uid,
        bitmapUri = bitmapUri,
        directoryUri = directoryUri,
        fileName = fileName,
        path = path,
        group = group,
        source = source,
        imageHeight = imageHeight,
        imageWidth = imageWidth,
        depth = depth,
        segmented = segmented,
        pose = pose,
        truncated = truncated,
        difficult = difficult,
        boxes = boxes
    )
}

