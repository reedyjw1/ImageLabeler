package com.reedy.imagelabeler.extensions

import android.util.Log
import com.reedy.imagelabeler.features.annotations.UiDocument

fun <T> List<T>.replace(newValue: T, block: (T) -> Boolean): List<T> {
    return map {
        if (block(it)) newValue else it
    }
}

fun <T> MutableList<T>.addAndUpdate(newValue: T): MutableList<T> {
    add(newValue)
    return this
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


