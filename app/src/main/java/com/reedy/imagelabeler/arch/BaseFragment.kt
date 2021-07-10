package com.reedy.imagelabeler.arch

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect

abstract class BaseFragment<S: ViewState, E: ViewStateEvent, X: ViewStateEffect, V: BaseViewModel<S, E, X>>(layoutResId: Int): Fragment(layoutResId) {
    abstract val viewModel: V

    abstract fun renderState(viewState: S)
    abstract fun handleSideEffect(effect: X)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenCreated {
            viewModel.state.collect { state ->
                if (isAdded) {
                    renderState(state)
                }
            }
        }
        viewModel.effects.startListening { effect ->
            if (isAdded) {
                handleSideEffect(effect)
            }
        }
    }
}