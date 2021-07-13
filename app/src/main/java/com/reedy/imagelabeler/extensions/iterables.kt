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
        if (it.uri == document.uri) {
            UiDocument(it.name, it.uri, true)
        } else {
            UiDocument(it.name, it.uri, false)
        }
    }.toMutableList()
}


