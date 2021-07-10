package com.reedy.imagelabeler.features.annotations.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.reedy.imagelabeler.R
import com.reedy.imagelabeler.arch.BaseFragment
import com.reedy.imagelabeler.view.overlay.Box
import com.reedy.imagelabeler.view.overlay.BoxAdded
import com.reedy.imagelabeler.view.overlay.Overlay
import kotlinx.android.synthetic.main.fragment_annotations.*

class AnnotationsFragment:
    BaseFragment<AnnotationsViewState, AnnotationsViewEvent, AnnotationsViewEffect, AnnotationsViewModel>(R.layout.fragment_annotations)
{
    companion object {
        private const val TAG = "Annotations"
    }

    //TODO - fix float to int conversion for better accuracy, fix scalling issue preventing boxes from being drawn, allow boxes to be edited, resize boxes with zoom.

    private val navigator by lazy {
        val navHostFragment = requireActivity()
            .supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navHostFragment.navController
    }
    override val viewModel: AnnotationsViewModel by viewModels {
        AnnotationsViewModelFactory(navigator)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        image_editor.setOnBoxAddedListener(object : BoxAdded {
            override fun onBoxAdded(box: Box) {
                image_editor.boxes.add(box)
                image_editor.invalidate()
            }

        })
        image_view.setImageResource(R.drawable.test)


        left.setOnClickListener { viewModel.process(AnnotationsViewEvent.LeftButtonClicked) }
        right.setOnClickListener { viewModel.process(AnnotationsViewEvent.RightButtonClicked) }
        edit.setOnClickListener { viewModel.process(AnnotationsViewEvent.EditButtonClicked) }
        delete.setOnClickListener { viewModel.process(AnnotationsViewEvent.DeleteButtonClicked) }

    }

    override fun renderState(viewState: AnnotationsViewState) {

    }

    override fun handleSideEffect(effect: AnnotationsViewEffect) {

    }
}