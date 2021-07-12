package com.reedy.imagelabeler.features.annotations.view

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.reedy.imagelabeler.R
import com.reedy.imagelabeler.arch.BaseFragment
import com.reedy.imagelabeler.generator.AnnotationGenerators
import com.reedy.imagelabeler.model.Box
import com.reedy.imagelabeler.view.image.BoxUpdatedListener
import kotlinx.android.synthetic.main.fragment_annotations.*
import java.io.File
import java.io.FileOutputStream

class AnnotationsFragment:
    BaseFragment<AnnotationsViewState, AnnotationsViewEvent, AnnotationsViewEffect, AnnotationsViewModel>(R.layout.fragment_annotations), BoxUpdatedListener
{
    companion object {
        private const val TAG = "Annotations"
        private const val REQUEST_CODE = 1274
    }

    private var treeUri: Uri? = null

    private val navigator by lazy {
        val navHostFragment = requireActivity()
            .supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navHostFragment.navController
    }
    override val viewModel: AnnotationsViewModel by viewModels {
        AnnotationsViewModelFactory(navigator)
    }

    private val adapter by lazy {
        DirectoryAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        image_editor.addBoxListener(this)
        overlay.setImageResource(R.drawable.bd6c0bef4a473bfca44d1f6c83c95006)
        left.setOnClickListener { viewModel.process(AnnotationsViewEvent.LeftButtonClicked) }
        right.setOnClickListener { viewModel.process(AnnotationsViewEvent.RightButtonClicked) }
        edit.setOnClickListener { viewModel.process(AnnotationsViewEvent.EditButtonClicked) }
        delete.setOnClickListener { viewModel.process(AnnotationsViewEvent.DeleteButtonClicked) }
        zoom.setOnClickListener { viewModel.process(AnnotationsViewEvent.ZoomButtonClicked) }
        export.setOnClickListener { viewModel.process(AnnotationsViewEvent.ExportFiles) }
        refresh.setOnClickListener { viewModel.process(AnnotationsViewEvent.RefreshDirectory) }
        directory_recycler.adapter = adapter
        askPermission()

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
        image_editor.updateBoxList(viewState.boxes)
        adapter.submitList(viewState.directory)
        title.text = viewState.directoryName
    }

    private fun enableZoom(bool: Boolean) {
        image_editor.setOverScrollHorizontal(bool)
        image_editor.setOverScrollVertical(bool)
        image_editor.setScrollEnabled(bool)
        image_editor.setHorizontalPanEnabled(bool)
        image_editor.setVerticalPanEnabled(bool)
        image_editor.isEditingEnabled(!bool)
        
    }

    override fun handleSideEffect(effect: AnnotationsViewEffect) {
        when(effect) {
            is AnnotationsViewEffect.UpdateBoxList -> {
                image_editor.updateBoxes(effect.box)
            }
            is AnnotationsViewEffect.ExportAnnotations -> export(effect.list)
            is AnnotationsViewEffect.RefreshDirectory -> {
                initDir()
            }
        }
    }

    private fun initDir() {
        val uri = treeUri ?: return
        val dir = DocumentFile.fromTreeUri(requireContext(), uri)
        val files = dir?.listFiles() ?: return
        val name = dir.name ?: return
        viewModel.process(AnnotationsViewEvent.UpdateDirectory(files.toMutableList(), name))

    }

    private fun export(boxes: List<Box>) {
        val uri = treeUri ?: return
        val dir = DocumentFile.fromTreeUri(requireContext(), uri)
        val file = dir?.createFile("*/image", "grid.jpg") ?: return
        val bitmap = (overlay.drawable as BitmapDrawable).bitmap

        requireActivity().contentResolver.openFileDescriptor(file.uri, "w")?.use { parcelFileDescriptor ->
            FileOutputStream(parcelFileDescriptor.fileDescriptor).use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
        }

        boxes.forEachIndexed { index, box ->
            box.imageHeight = bitmap.height
            box.imageWidth = bitmap.width

            val xMin = box.relativeToBitmapXMin ?: return
            val yMin = box.relativeToBitmapYMin ?: return
            val xMax = box.relativeToBitmapXMax ?: return
            val yMax = box.relativeToBitmapYMax ?: return

            if (xMin > xMax) {
                box.relativeToBitmapXMin = xMax
                box.relativeToBitmapXMax = xMin
            }
            if (yMin > yMax) {
                box.relativeToBitmapYMin = yMax
                box.relativeToBitmapYMax = yMin
            }
            
            val generatedText = AnnotationGenerators.getPascalVocAnnotation(box)

            val xmlFile = dir.createFile("*/txt", "grid_${index}.xml") ?: return@forEachIndexed

            requireActivity().contentResolver.openFileDescriptor(xmlFile.uri, "w")?.use { parcelFileDescriptor ->
                FileOutputStream(parcelFileDescriptor.fileDescriptor).use {
                    it.write(generatedText.toByteArray())
                }
            }
        }
        initDir()
    }

    private fun askPermission() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)

        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data != null) {
                //this is the uri user has provided us
                treeUri = data.data
                initDir()

            }
        }
    }

    override fun onBoxAdded(box: Box, onlyVisual: Boolean) {
        viewModel.process(AnnotationsViewEvent.OnBoxAdded(box, onlyVisual))
    }
}