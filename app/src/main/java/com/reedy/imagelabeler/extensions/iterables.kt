package com.reedy.imagelabeler.extensions

fun <T> List<T>.replace(newValue: T, block: (T) -> Boolean): List<T> {
    return map {
        if (block(it)) newValue else it
    }
}

fun <T> List<T>.addAndUpdate(newValue: T): List<T> {
    this.toMutableList().add(newValue)
    return this
}

