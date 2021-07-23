package com.reedy.imagelabeler.features.projects.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.reedy.imagelabeler.R
import com.reedy.imagelabeler.arch.BaseFragment
import kotlinx.android.synthetic.main.fragment_projects.*

class ProjectsFragment:
    BaseFragment<ProjectsViewState, ProjectsViewEvent, ProjectsViewEffect, ProjectsViewModel>(R.layout.fragment_projects)
{
    private val navigator by lazy {
        val navHostFragment = requireActivity()
            .supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navHostFragment.navController
    }
    override val viewModel: ProjectsViewModel by viewModels {
        ProjectsViewModelFactory(navigator)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        create_new_project.setOnClickListener { viewModel.process(ProjectsViewEvent.GotoCreateProjectDialog) }
    }

    override fun renderState(viewState: ProjectsViewState) {

    }

    override fun handleSideEffect(effect: ProjectsViewEffect) {

    }
}