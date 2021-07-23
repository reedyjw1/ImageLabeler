package com.reedy.imagelabeler.features.projects.dialog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.reedy.imagelabeler.arch.BaseViewModel
import com.reedy.imagelabeler.features.projects.view.ProjectsViewModel

class CreateProjectsViewModel private constructor(
    savedStateHandle: SavedStateHandle,
    navigator: NavController
): BaseViewModel<CreateProjectsViewState, CreateProjectsViewEvent, CreateProjectsViewEffect>(
    initialState = CreateProjectsViewState()
) {
    override fun process(event: CreateProjectsViewEvent) {

    }

    companion object {
        internal fun create(
            stateHandle: SavedStateHandle,
            navigator: NavController
        ): CreateProjectsViewModel {
            return CreateProjectsViewModel(
                stateHandle,
                navigator
            )
        }
    }
}

class CreateProjectViewModelFactory(
    private val navigator: NavController
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CreateProjectsViewModel.create(
            stateHandle = SavedStateHandle(),
            navigator = navigator
        ) as T
    }

}