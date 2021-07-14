package com.reedy.imagelabeler.features.annotations.view

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.reedy.imagelabeler.arch.ViewState
import com.reedy.imagelabeler.arch.ViewStateEffect
import com.reedy.imagelabeler.arch.ViewStateEvent
import com.reedy.imagelabeler.features.annotations.UiDocument
import com.reedy.imagelabeler.model.Annotation
import com.reedy.imagelabeler.model.Box
import com.reedy.imagelabeler.model.MetaData

data class AnnotationsViewState(
    val buttonState: ButtonState = ButtonState.ZOOM,
    val annotation: MetaData = MetaData(),
    val directory: MutableList<UiDocument> = mutableListOf(),
    val directoryName: String = ""
): ViewState

sealed class AnnotationsViewEvent(): ViewStateEvent {
    object LeftButtonClicked: AnnotationsViewEvent()
    object RightButtonClicked: AnnotationsViewEvent()
    object EditButtonClicked: AnnotationsViewEvent()
    object DeleteButtonClicked: AnnotationsViewEvent()
    object ZoomButtonClicked: AnnotationsViewEvent()
    object ExportFiles: AnnotationsViewEvent()
    object RefreshDirectory: AnnotationsViewEvent()
    data class SaveAnnotationToDB(val metadata: MetaData): AnnotationsViewEvent()
    data class OnBoxAdded(val box: Box, val onlyVisual: Boolean): AnnotationsViewEvent()
    data class UpdateDirectory(val dir: MutableList<DocumentFile>, val name: String, val isFirstUpdate: Boolean = false): AnnotationsViewEvent()
    data class FileClicked(val document: UiDocument): AnnotationsViewEvent()
}

sealed class AnnotationsViewEffect(): ViewStateEffect {
    data class UpdateBoxList(val box: Box): AnnotationsViewEffect()
    data class ExportAnnotations(val annotation: Annotation): AnnotationsViewEffect()
    data class LoadImage(val doc: UiDocument?): AnnotationsViewEffect()
    object RefreshDirectory: AnnotationsViewEffect()
}