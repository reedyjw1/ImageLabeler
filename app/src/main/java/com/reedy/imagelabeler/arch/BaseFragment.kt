package com.reedy.imagelabeler.arch

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.reedy.imagelabeler.R
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.coroutines.flow.collect

abstract class BaseFragment<S: ViewState, E: ViewStateEvent, X: ViewStateEffect, V: BaseViewModel<S, E, X>>(layoutResId: Int): Fragment(layoutResId) {
    abstract val viewModel: V

    abstract fun renderState(viewState: S)
    abstract fun handleSideEffect(effect: X)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with (requireActivity() as AppCompatActivity) {
            setSupportActionBar(toolbar)

            val navController = NavHostFragment.findNavController(this@BaseFragment)

            setupActionBar(this, toolbar, navController)
        }

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

    open fun setupActionBar(
        activity: AppCompatActivity,
        toolbar: Toolbar,
        navController: NavController
    ) {
        NavigationUI.setupActionBarWithNavController(activity, navController)
    }


    open fun showUpNavigation() {
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener { onNavigationClick() }

    }

    open fun removeNavigation() {
        toolbar.setNavigationOnClickListener {  }
        toolbar.navigationIcon = null
    }

    open fun onNavigationClick() {
        findNavController().navigateUp()
    }

}