package com.reedy.imagelabeler.extensions

fun <T> List<T>.replace(newValue: T, block: (T) -> Boolean): List<T> {
    return map {
        if (block(it)) newValue else it
    }
}

fun <T> MutableList<T>.addAndUpdate(newValue: T): MutableList<T> {
    add(newValue)
    return this
}

