package com.reedy.imagelabeler.features.annotations.view

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.reedy.imagelabeler.arch.BaseViewModel
import com.reedy.imagelabeler.extensions.addAndUpdate
import com.reedy.imagelabeler.extensions.findFirstImage
import com.reedy.imagelabeler.extensions.updateSelected
import com.reedy.imagelabeler.features.annotations.UiDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AnnotationsViewModel private constructor(
    savedStateHandle: SavedStateHandle,
    navigator: NavController
): BaseViewModel<AnnotationsViewState, AnnotationsViewEvent, AnnotationsViewEffect>(
    initialState = AnnotationsViewState()
){
    override fun process(event: AnnotationsViewEvent) {
        when(event) {
            AnnotationsViewEvent.LeftButtonClicked -> {

            }
            AnnotationsViewEvent.RightButtonClicked -> {

            }
            AnnotationsViewEvent.EditButtonClicked -> {
                setState {
                    copy(
                        buttonState = ButtonState.EDIT
                    )
                }
            }
            AnnotationsViewEvent.DeleteButtonClicked -> {
                setState {
                    copy(
                        buttonState = ButtonState.DELETE
                    )
                }
            }
            AnnotationsViewEvent.ZoomButtonClicked -> {
                setState {
                    copy(
                        buttonState = ButtonState.ZOOM
                    )
                }
            }
            is AnnotationsViewEvent.UpdateDirectory -> {
                viewModelScope.launch(Dispatchers.Default) {
                    val dir: MutableList<UiDocument> = event.dir.map {
                        UiDocument(name = it.name ?: "[No Name]", uri = it.uri, type = it.type ?: "none")
                    }.toMutableList()

                    setState {
                        copy(
                            directoryName = event.name,
                            directory = dir
                        )
                    }
                    if (event.isFirstUpdate) {
                        withContext(Dispatchers.Main) {
                            val document = viewState.value.directory.findFirstImage()
                            emitEffect(AnnotationsViewEffect.LoadImage(document))
                        }
                    }
                }
            }
            is AnnotationsViewEvent.RefreshDirectory -> {
                emitEffect(AnnotationsViewEffect.RefreshDirectory)
            }
            is AnnotationsViewEvent.FileClicked -> {
                viewModelScope.launch(Dispatchers.Default) {
                    setState {
                        copy(
                            directory = directory.updateSelected(event.document)
                        )
                    }
                }
                emitEffect(AnnotationsViewEffect.LoadImage(event.document))
            }
            is AnnotationsViewEvent.OnBoxAdded -> {
                // For updating the list while the box is still moving
                emitEffect(AnnotationsViewEffect.UpdateBoxList(event.box))

                // For finalizing the box list once the touch has been released
                // Ensures that the view model is the ultimate source of truth
                viewModelScope.launch(Dispatchers.Default) {
                    if (!event.onlyVisual) {
                        setState {
                            copy(
                                boxes = boxes.addAndUpdate(event.box)
                            )
                        }
                    }
                }
            }
            AnnotationsViewEvent.ExportFiles -> {
                emitEffect(AnnotationsViewEffect.ExportAnnotations(viewState.value.boxes))
            }
        }
    }

    companion object {
        internal fun create(
            stateHandle: SavedStateHandle,
            navigator: NavController
        ): AnnotationsViewModel {
            return AnnotationsViewModel(
                stateHandle,
                navigator
            )
        }
    }
}

class AnnotationsViewModelFactory(
    private val navigator: NavController
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AnnotationsViewModel.create(
            stateHandle = SavedStateHandle(),
            navigator = navigator
        ) as T
    }

}