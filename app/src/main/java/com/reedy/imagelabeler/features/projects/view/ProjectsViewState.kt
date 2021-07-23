package com.reedy.imagelabeler.features.projects.view

import com.reedy.imagelabeler.arch.ViewState
import com.reedy.imagelabeler.arch.ViewStateEffect
import com.reedy.imagelabeler.arch.ViewStateEvent
import com.reedy.imagelabeler.features.projects.model.UiProject

data class ProjectsViewState(
    val projects: List<UiProject> = emptyList()
): ViewState

sealed class ProjectsViewEvent(): ViewStateEvent {
    object GotoCreateProjectDialog: ProjectsViewEvent()
}

sealed class ProjectsViewEffect(): ViewStateEffect {

}