package com.reedy.imagelabeler.features.annotations.view

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.reedy.imagelabeler.arch.BaseViewModel
import com.reedy.imagelabeler.extensions.*
import com.reedy.imagelabeler.features.annotations.model.ButtonState
import com.reedy.imagelabeler.features.annotations.model.UiDocument
import com.reedy.imagelabeler.features.annotations.repository.IAnnotationsRepository
import com.reedy.imagelabeler.model.Box
import com.reedy.imagelabeler.model.ImageData
import com.reedy.imagelabeler.model.checkAndSwap
import com.reedy.imagelabeler.model.removeAndUpdate
import com.reedy.imagelabeler.utils.shared.ISharedPrefsHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*

class AnnotationsViewModel private constructor(
    savedStateHandle: SavedStateHandle,
    navigator: NavController
): BaseViewModel<AnnotationsViewState, AnnotationsViewEvent, AnnotationsViewEffect>(
    initialState = AnnotationsViewState()
), KoinComponent {



    private val repository: IAnnotationsRepository by inject()
    private var undoList = Stack<Box>()
    private var redoList = Stack<Box>()

    override fun process(event: AnnotationsViewEvent) {
        when(event) {
            is AnnotationsViewEvent.SaveAnnotationToDB -> {

            }
            is AnnotationsViewEvent.LeftButtonClicked -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val imgData = viewState.value.imageData?.copy() ?: return@launch
                    saveSelectedAnnotation(imgData)
                    leftOrRight(false)
                }
            }
            is AnnotationsViewEvent.RightButtonClicked -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val imgData = viewState.value.imageData?.copy() ?: return@launch
                    saveSelectedAnnotation(imgData)
                    leftOrRight(true)
                }

            }
            AnnotationsViewEvent.OnUndo -> {
                if (undoList.isEmpty()) return
                val removed = undoList.pop()
                redoList.add(removed)
                setState {
                    copy(
                        imageData = imageData?.removeAndUpdate(removed)
                    )
                }
                emitEffect(AnnotationsViewEffect.UpdateEntireList(viewState.value.imageData?.boxes ?: mutableListOf()))
            }
            AnnotationsViewEvent.OnRedo -> {
                if (redoList.isEmpty()) return
                val redo = redoList.pop()
                undoList.add(redo)
                setState {
                    copy(
                        imageData = imageData?.addAndUpdate(redo)
                    )
                }
                emitEffect(AnnotationsViewEffect.UpdateEntireList(viewState.value.imageData?.boxes ?: mutableListOf()))
            }
            AnnotationsViewEvent.OnClear -> {
                setState {
                    copy(
                        imageData = imageData?.resetBoxes()
                    )
                }
                emitEffect(AnnotationsViewEffect.UpdateEntireList(viewState.value.imageData?.boxes ?: mutableListOf()))
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
                viewModelScope.launch(Dispatchers.IO) {
                    val dir: MutableList<UiDocument> = event.dir.map {
                        UiDocument(name = it.name ?: "[No Name]", uri = it.uri, type = it.type ?: "none")
                    }.toMutableList()

                    withContext(Dispatchers.Main) {
                        setState {
                            copy(
                                directoryName = event.name,
                                directory = dir,
                            )
                        }
                    }
                    if (event.isFirstUpdate) {
                        val document = viewState.value.directory.findFirstImage() ?: return@launch
                        val imageData = getNewOrActualImageData(document)
                        withContext(Dispatchers.Main) {
                            setState {
                                copy(
                                    directory = directory.updateSelected(document),
                                    imageData = imageData
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
                setState {
                    copy(
                        directory = directory.updateSelected(event.document),
                    )
                }
                viewModelScope.launch(Dispatchers.IO) {
                    val oldImg = viewState.value.imageData ?: return@launch
                    val imageData = getNewOrActualImageData(event.document)
                    undoList = Stack<Box>()
                    redoList = Stack<Box>()
                    setState {
                        copy(
                            imageData = imageData
                        )
                    }
                    saveSelectedAnnotation(oldImg)
                    withContext(Dispatchers.Main) {
                        emitEffect(AnnotationsViewEffect.LoadImage(event.document))
                    }
                }
            }
            is AnnotationsViewEvent.OnBoxAdded -> {
                // For updating the list while the box is still moving
                emitEffect(AnnotationsViewEffect.UpdateBoxList(event.box))

                // For finalizing the box list once the touch has been released
                // Ensures that the view model is the ultimate source of truth
                viewModelScope.launch(Dispatchers.IO) {
                    if (!event.onlyVisual) {
                        val annotation = viewState.value.imageData?.addAndUpdate(event.box) ?: return@launch
                        annotation.boxes = annotation.boxes.checkAndSwap()
                        if (undoList.size > 9) {
                            undoList.removeFirst()
                            undoList.add(event.box.checkAndSwap())
                        } else {
                            undoList.add(event.box.checkAndSwap())
                        }
                        setState {
                            copy(
                                imageData = annotation
                            )
                        }
                    }
                }
            }
            AnnotationsViewEvent.ExportFiles -> {
                val imageData = viewState.value.imageData ?: return
                emitEffect(AnnotationsViewEffect.ExportAnnotations(imageData))
            }
            AnnotationsViewEvent.OnStop -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val imgData = viewState.value.imageData?.copy() ?: return@launch
                    undoList = Stack<Box>()
                    redoList = Stack<Box>()
                    saveSelectedAnnotation(imgData)
                }
            }
        }
    }

    private suspend fun getNewOrActualImageData(document: UiDocument): ImageData {
        return repository.loadByImageUri(document.uri.toString()) ?: ImageData(bitmapUri = document.uri.toString())
    }

    private suspend fun leftOrRight(right: Boolean) {
        val currentDisplay = viewState.value.directory.findSelected() ?: return
        val document = if (right)
            viewState.value.directory.findNext(currentDisplay) ?: return
        else
            viewState.value.directory.findPrevious(currentDisplay) ?: return
        val imgData = getNewOrActualImageData(document)
        undoList = Stack<Box>()
        redoList = Stack<Box>()
        withContext(Dispatchers.Main) {
            setState {
                copy(
                    imageData = imgData,
                    directory = directory.updateSelected(document)
                )
            }
            emitEffect(AnnotationsViewEffect.LoadImage(document))
        }
    }

    private suspend fun saveSelectedAnnotation(imageData: ImageData) {
        repository.saveAnnotations(imageData)
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
        private const val TAG = "AnnotationsViewModel"
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