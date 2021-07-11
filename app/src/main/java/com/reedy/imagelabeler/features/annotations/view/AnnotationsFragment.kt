package com.reedy.imagelabeler.features.annotations.view

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.reedy.imagelabeler.R
import com.reedy.imagelabeler.arch.BaseFragment
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

        overlay.setImageResource(R.drawable.grid)
        //val bitmap = (image.drawable as BitmapDrawable).bitmap
        //overlay.setImageBitmap(Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888))

        left.setOnClickListener { viewModel.process(AnnotationsViewEvent.LeftButtonClicked) }
        right.setOnClickListener { viewModel.process(AnnotationsViewEvent.RightButtonClicked) }
        edit.setOnClickListener { viewModel.process(AnnotationsViewEvent.EditButtonClicked) }
        delete.setOnClickListener { viewModel.process(AnnotationsViewEvent.DeleteButtonClicked) }
        zoom.setOnClickListener { viewModel.process(AnnotationsViewEvent.ZoomButtonClicked) }

    }

    override fun renderState(viewState: AnnotationsViewState) {
        Log.i(TAG, "renderState: ${viewState.buttonState}")
        when(viewState.buttonState) {
            ButtonState.ZOOM -> {
                enableZoom(true)
            }
            ButtonState.EDIT -> {
                enableZoom(false)
            }
            ButtonState.DELETE -> {
                enableZoom(true)
            }
        }
    }

    private fun enableZoom(bool: Boolean) {
        image_editor.setOverScrollHorizontal(bool)
        image_editor.setOverScrollVertical(bool)
        image_editor.setScrollEnabled(bool)
        image_editor.setHorizontalPanEnabled(bool)
        image_editor.setVerticalPanEnabled(bool)
        image_editor.isEditing = !bool
        
    }

    override fun handleSideEffect(effect: AnnotationsViewEffect) {

    }
}