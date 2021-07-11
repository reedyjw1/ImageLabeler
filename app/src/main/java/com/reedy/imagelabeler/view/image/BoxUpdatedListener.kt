package com.reedy.imagelabeler.view.image

import com.reedy.imagelabeler.model.Box

interface BoxUpdatedListener {
    fun onBoxAdded(box: Box, onlyVisual: Boolean = false)
}