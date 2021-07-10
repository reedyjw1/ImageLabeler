package com.reedy.imagelabeler.arch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<S: ViewState, E: ViewStateEvent, X: ViewStateEffect>(
    private val initialState: S
): ViewModel(), ViewModelContract<E> {

    protected var viewState: MutableStateFlow<S> = MutableStateFlow(initialState)
        private set

    val state: StateFlow<S> = viewState

    protected val effectsEmitter = EventEmitter<X>()
    val effects: EventSource<X> get() = effectsEmitter

    init {
        setState {initialState}
    }

    protected fun setState(stateModifier: S.() -> S) {
        viewModelScope.launch {
            viewState.value = stateModifier.invoke(viewState.value)
        }
    }

    protected fun emitEffect(effect: X) {
        effectsEmitter.emit(effect)
    }


}