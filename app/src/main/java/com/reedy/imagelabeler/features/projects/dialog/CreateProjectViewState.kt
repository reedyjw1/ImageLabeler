package com.reedy.imagelabeler.features.projects.dialog

import com.reedy.imagelabeler.arch.ViewState
import com.reedy.imagelabeler.arch.ViewStateEffect
import com.reedy.imagelabeler.arch.ViewStateEvent
import com.reedy.imagelabeler.features.annotations.model.UiLabel
import java.util.*

data class CreateProjectsViewState(
    val projectId: String = UUID.randomUUID().toString(),
    val projectName: String = "",
    val license: String = "",
    val datasetUrl: String = ""
): ViewState

sealed class CreateProjectsViewEvent(): ViewStateEvent {

}

sealed class CreateProjectsViewEffect(): ViewStateEffect {

}