package com.reedy.imagelabeler.features.projects.dialog

import android.graphics.Point
import android.view.Gravity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.reedy.imagelabeler.R
import com.reedy.imagelabeler.arch.BaseDialogFragment

class CreateProjectDialog:
    BaseDialogFragment<CreateProjectsViewState, CreateProjectsViewEvent, CreateProjectsViewEffect, CreateProjectsViewModel>(
        R.layout.dialog_add_new_project) {

    private val navigator by lazy {
        val navHostFragment = requireActivity()
            .supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navHostFragment.navController
    }
    override val viewModel: CreateProjectsViewModel by viewModels {
        CreateProjectViewModelFactory(navigator)
    }

    override fun renderState(viewState: CreateProjectsViewState) {

    }

    override fun handleSideEffect(effect: CreateProjectsViewEffect) {

    }

    override fun onResume() {
        super.onResume()
        val display = dialog?.window?.windowManager?.defaultDisplay
        val size = Point()
        display?.getSize(size)
        val width: Int = size.x
        val height: Int = size.y
        dialog?.window?.setLayout((width * .7f).toInt(), (height * .70f).toInt())
        dialog?.window?.setGravity(Gravity.CENTER)
    }

}