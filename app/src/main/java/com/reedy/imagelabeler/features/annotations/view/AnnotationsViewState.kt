package com.reedy.imagelabeler.features.annotations.view

import androidx.documentfile.provider.DocumentFile
import com.reedy.imagelabeler.arch.ViewState
import com.reedy.imagelabeler.arch.ViewStateEffect
import com.reedy.imagelabeler.arch.ViewStateEvent
import com.reedy.imagelabeler.features.annotations.model.ButtonState
import com.reedy.imagelabeler.features.annotations.model.UiDocument
import com.reedy.imagelabeler.model.Box
import com.reedy.imagelabeler.model.ImageData

data class AnnotationsViewState(
    val buttonState: ButtonState = ButtonState.ZOOM,
    val imageData: ImageData? = null,
    val directory: MutableList<UiDocument> = mutableListOf(),
    val directoryName: String = ""
): ViewState

sealed class AnnotationsViewEvent: ViewStateEvent {
    object LeftButtonClicked: AnnotationsViewEvent()
    object RightButtonClicked: AnnotationsViewEvent()
    object EditButtonClicked: AnnotationsViewEvent()
    object DeleteButtonClicked: AnnotationsViewEvent()
    object ZoomButtonClicked: AnnotationsViewEvent()
    object ExportFiles: AnnotationsViewEvent()
    object RefreshDirectory: AnnotationsViewEvent()
    object OnStop: AnnotationsViewEvent()
    object OnUndo: AnnotationsViewEvent()
    object OnRedo: AnnotationsViewEvent()
    data class SaveAnnotationToDB(val metadata: ImageData): AnnotationsViewEvent()
    data class OnBoxAdded(val box: Box, val onlyVisual: Boolean): AnnotationsViewEvent()
    data class UpdateDirectory(val dir: MutableList<DocumentFile>, val name: String, val isFirstUpdate: Boolean = false): AnnotationsViewEvent()
    data class FileClicked(val document: UiDocument): AnnotationsViewEvent()
}

sealed class AnnotationsViewEffect: ViewStateEffect {
    data class UpdateBoxList(val box: Box): AnnotationsViewEffect()
    data class ExportAnnotations(val annotation: ImageData): AnnotationsViewEffect()
    data class LoadImage(val doc: UiDocument?): AnnotationsViewEffect()
    object RefreshDirectory: AnnotationsViewEffect()
    data class UpdateEntireList(val boxes: MutableList<Box>): AnnotationsViewEffect()
}