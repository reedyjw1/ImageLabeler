package com.reedy.imagelabeler.view.image

interface BoxUpdatedListener {
    fun onBoxAdded(box: Box, onlyVisual: Boolean = false)
}