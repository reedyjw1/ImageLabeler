package com.reedy.imagelabeler.features.projects.view

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.reedy.imagelabeler.R
import com.reedy.imagelabeler.arch.BaseViewModel

class ProjectsViewModel private constructor(
    savedStateHandle: SavedStateHandle,
    private val navigator: NavController
): BaseViewModel<ProjectsViewState, ProjectsViewEvent, ProjectsViewEffect>(
    initialState = ProjectsViewState()
){
    override fun process(event: ProjectsViewEvent) {
        when(event) {
            ProjectsViewEvent.GotoCreateProjectDialog -> {
                navigator.navigate(R.id.gotoCreateNewProject)
            }
        }
    }

    companion object {
        internal fun create(
            stateHandle: SavedStateHandle,
            navigator: NavController
        ): ProjectsViewModel {
            return ProjectsViewModel(
                stateHandle,
                navigator
            )
        }
    }
}

class ProjectsViewModelFactory(
    private val navigator: NavController
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProjectsViewModel.create(
            stateHandle = SavedStateHandle(),
            navigator = navigator
        ) as T
    }

}