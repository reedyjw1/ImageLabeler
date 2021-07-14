package com.reedy.imagelabeler.features.annotations.view

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.reedy.imagelabeler.arch.BaseViewModel
import com.reedy.imagelabeler.extensions.*
import com.reedy.imagelabeler.features.annotations.UiDocument
import com.reedy.imagelabeler.model.checkAndSwap
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
            is AnnotationsViewEvent.SaveAnnotationToDB -> {

            }
            is AnnotationsViewEvent.LeftButtonClicked -> {
                leftOrRight(false)
            }
            is AnnotationsViewEvent.RightButtonClicked -> {
                leftOrRight(true)

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
                            directory = dir,
                        )
                    }
                    if (event.isFirstUpdate) {
                        withContext(Dispatchers.Main) {
                            val document = viewState.value.directory.findFirstImage() ?: return@withContext
                            setState {
                                copy(
                                    directory = directory.updateSelected(document)
                                )
                            }
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
                        val annotation = viewState.value.annotation.addAndUpdate(event.box)
                        annotation.annotation.boxes = annotation.annotation.boxes.checkAndSwap()
                        setState {
                            copy(
                                annotation = annotation
                            )
                        }
                    }
                }
            }
            AnnotationsViewEvent.ExportFiles -> {
                emitEffect(AnnotationsViewEffect.ExportAnnotations(viewState.value.annotation.annotation))
            }
        }
    }

    private fun leftOrRight(right: Boolean) {
        val currentDisplay = viewState.value.directory.findSelected() ?: return
        val document = if (right)
            viewState.value.directory.findNext(currentDisplay) ?: return
        else
            viewState.value.directory.findPrevious(currentDisplay) ?: return
        setState {
            copy(
                annotation = annotation.resetBoxes(),
                directory = directory.updateSelected(document)
            )
        }
        emitEffect(AnnotationsViewEffect.LoadImage(document))
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