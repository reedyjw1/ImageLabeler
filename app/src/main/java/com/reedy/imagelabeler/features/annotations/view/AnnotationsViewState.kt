package com.reedy.imagelabeler.features.annotations.view

import com.reedy.imagelabeler.arch.ViewState
import com.reedy.imagelabeler.arch.ViewStateEffect
import com.reedy.imagelabeler.arch.ViewStateEvent

data class AnnotationsViewState(
    val buttonState: ButtonState = ButtonState.ZOOM
): ViewState

sealed class AnnotationsViewEvent(): ViewStateEvent {
    object LeftButtonClicked: AnnotationsViewEvent()
    object RightButtonClicked: AnnotationsViewEvent()
    object EditButtonClicked: AnnotationsViewEvent()
    object DeleteButtonClicked: AnnotationsViewEvent()
    object ZoomButtonClicked: AnnotationsViewEvent()
}

sealed class AnnotationsViewEffect(): ViewStateEffect {

}