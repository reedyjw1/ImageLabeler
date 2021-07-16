package com.reedy.imagelabeler.features.annotations.view

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.reedy.imagelabeler.R
import com.reedy.imagelabeler.arch.BaseFragment
import com.reedy.imagelabeler.features.annotations.model.ButtonState
import com.reedy.imagelabeler.utils.AnnotationGenerators
import com.reedy.imagelabeler.model.Box
import com.reedy.imagelabeler.model.ImageData
import com.reedy.imagelabeler.utils.shared.ISharedPrefsHelper
import com.reedy.imagelabeler.utils.shared.SharedPrefsKeys
import com.reedy.imagelabeler.view.image.BoxUpdatedListener
import kotlinx.android.synthetic.main.fragment_annotations.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.FileOutputStream
import java.lang.Exception

class AnnotationsFragment:
    BaseFragment<AnnotationsViewState, AnnotationsViewEvent, AnnotationsViewEffect, AnnotationsViewModel>
        (R.layout.fragment_annotations), BoxUpdatedListener, KoinComponent
{
    companion object {
        private const val TAG = "Annotations"
        private const val REQUEST_CODE = 1274
    }

    private val navigator by lazy {
        val navHostFragment = requireActivity()
            .supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navHostFragment.navController
    }
    private val sharedProvider: ISharedPrefsHelper by inject()

    override val viewModel: AnnotationsViewModel by viewModels {
        AnnotationsViewModelFactory(navigator)
    }

    private val adapter by lazy {
        DirectoryAdapter().apply {
            onClick = {
                viewModel.process(AnnotationsViewEvent.FileClicked(it))
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        image_editor.addBoxListener(this)
        left.setOnClickListener { viewModel.process(AnnotationsViewEvent.LeftButtonClicked) }
        right.setOnClickListener { viewModel.process(AnnotationsViewEvent.RightButtonClicked) }
        edit.setOnClickListener { viewModel.process(AnnotationsViewEvent.EditButtonClicked) }
        delete.setOnClickListener { viewModel.process(AnnotationsViewEvent.DeleteButtonClicked) }
        zoom.setOnClickListener { viewModel.process(AnnotationsViewEvent.ZoomButtonClicked) }
        export.setOnClickListener { viewModel.process(AnnotationsViewEvent.ExportFiles) }
        refresh.setOnClickListener { viewModel.process(AnnotationsViewEvent.RefreshDirectory) }
        undo.setOnClickListener { viewModel.process(AnnotationsViewEvent.OnUndo) }
        directory_recycler.adapter = adapter
        askPermission()

    }

    override fun renderState(viewState: AnnotationsViewState) {
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
        Log.i(TAG, "renderState: rendering")
        val boxesSafe = viewState.imageData?.boxes ?: return
        image_editor.updateBoxList(boxesSafe)
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
            is AnnotationsViewEffect.UpdateEntireList -> { image_editor.updateBoxList(effect.boxes) }
            is AnnotationsViewEffect.ExportAnnotations -> export(effect.annotation)
            is AnnotationsViewEffect.RefreshDirectory -> {
                initDir()
            }
            is AnnotationsViewEffect.LoadImage -> {
                if (effect.doc == null) {
                    image_editor.visibility = View.INVISIBLE
                    return
                }
                if (!effect.doc.type.contains("image")) {
                    image_editor.visibility = View.INVISIBLE
                    return
                }
                image_editor.visibility = View.VISIBLE
                try {
                    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver, effect.doc.uri))
                    } else {
                        MediaStore.Images.Media.getBitmap(requireContext().contentResolver, effect.doc.uri)
                    }
                    overlay.setImageDrawable(bitmap.toDrawable(requireActivity().resources))
                } catch (e: Exception) {
                    Log.e(TAG, "handleSideEffect: Could not display image=${e.localizedMessage}")
                    image_editor.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun initDir(isFirst: Boolean = false) {
        val uri = sharedProvider.getSharedPrefs(SharedPrefsKeys.DIR_URI)?.toUri() ?: return
        val dir = DocumentFile.fromTreeUri(requireContext(), uri)
        val files = dir?.listFiles() ?: return
        val name = dir.name ?: return
        viewModel.process(AnnotationsViewEvent.UpdateDirectory(files.toMutableList(), name, isFirst))
    }

    private fun export(annotation: ImageData) {
        val uri = sharedProvider.getSharedPrefs(SharedPrefsKeys.DIR_URI)?.toUri() ?: return
        val dir = DocumentFile.fromTreeUri(requireContext(), uri)
        val file = dir?.createFile("image", "grid.jpg") ?: return
        val bitmap = (overlay.drawable as BitmapDrawable).bitmap

        requireActivity().contentResolver.openFileDescriptor(file.uri, "w")?.use { parcelFileDescriptor ->
            FileOutputStream(parcelFileDescriptor.fileDescriptor).use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
        }

        annotation.imageHeight = bitmap.height
        annotation.imageWidth = bitmap.width

        annotation.boxes.forEachIndexed { index, box ->
            val generatedText = AnnotationGenerators.getPascalVocAnnotation(box, annotation)

            val xmlFile = dir.createFile("application/xml", "grid_${index}.xml") ?: return@forEachIndexed

            requireActivity().contentResolver.openFileDescriptor(xmlFile.uri, "w")?.use { parcelFileDescriptor ->
                FileOutputStream(parcelFileDescriptor.fileDescriptor).use {
                    it.write(generatedText.toByteArray())
                }
            }
        }
        initDir()
    }

    private fun askPermission() {
        val uri = sharedProvider.getSharedPrefs(SharedPrefsKeys.DIR_URI)?.toUri()
        if (uri == null) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            startActivityForResult(intent, REQUEST_CODE)
        } else {
            initDir(true)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data != null) {
                //this is the uri user has provided us
                sharedProvider.saveToSharedPrefs(SharedPrefsKeys.DIR_URI, data.data.toString())
                initDir(true)
            }
        }
    }

    override fun onBoxAdded(box: Box, onlyVisual: Boolean) {
        viewModel.process(AnnotationsViewEvent.OnBoxAdded(box, onlyVisual))
    }

    override fun onStop() {
        viewModel.process(AnnotationsViewEvent.OnStop)
        super.onStop()
    }
}