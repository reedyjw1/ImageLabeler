package com.reedy.imagelabeler.features.annotations.view

import com.reedy.imagelabeler.arch.ViewState
import com.reedy.imagelabeler.arch.ViewStateEffect
import com.reedy.imagelabeler.arch.ViewStateEvent
import com.reedy.imagelabeler.model.Box

data class AnnotationsViewState(
    val buttonState: ButtonState = ButtonState.ZOOM,
    val boxes: MutableList<Box> = mutableListOf()
): ViewState

sealed class AnnotationsViewEvent(): ViewStateEvent {
    object LeftButtonClicked: AnnotationsViewEvent()
    object RightButtonClicked: AnnotationsViewEvent()
    object EditButtonClicked: AnnotationsViewEvent()
    object DeleteButtonClicked: AnnotationsViewEvent()
    object ZoomButtonClicked: AnnotationsViewEvent()
    object ExportFiles: AnnotationsViewEvent()
    data class OnBoxAdded(val box: Box, val onlyVisual: Boolean): AnnotationsViewEvent()
}

sealed class AnnotationsViewEffect(): ViewStateEffect {
    data class UpdateBoxList(val box: Box): AnnotationsViewEffect()
    data class ExportAnnotations(val list: List<Box>): AnnotationsViewEffect()
}