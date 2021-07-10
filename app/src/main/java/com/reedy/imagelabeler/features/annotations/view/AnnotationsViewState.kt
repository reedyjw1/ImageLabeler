package com.reedy.imagelabeler.features.annotations.view

import com.reedy.imagelabeler.arch.ViewState
import com.reedy.imagelabeler.arch.ViewStateEffect
import com.reedy.imagelabeler.arch.ViewStateEvent

data class AnnotationsViewState(
    val name: String = ""
): ViewState

sealed class AnnotationsViewEvent(): ViewStateEvent {
    object LeftButtonClicked: AnnotationsViewEvent()
    object RightButtonClicked: AnnotationsViewEvent()
    object EditButtonClicked: AnnotationsViewEvent()
    object DeleteButtonClicked: AnnotationsViewEvent()
}

sealed class AnnotationsViewEffect(): ViewStateEffect {

}