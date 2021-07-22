package com.reedy.imagelabeler.features.annotations.view

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.UriPermission
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import androidx.core.view.iterator
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.reedy.imagelabeler.R
import com.reedy.imagelabeler.arch.BaseFragment
import com.reedy.imagelabeler.features.annotations.model.ButtonState
import com.reedy.imagelabeler.features.annotations.model.UiLabel
import com.reedy.imagelabeler.model.Box
import com.reedy.imagelabeler.model.ExportState
import com.reedy.imagelabeler.model.ImageData
import com.reedy.imagelabeler.utils.AnnotationGenerators
import com.reedy.imagelabeler.utils.shared.ISharedPrefsHelper
import com.reedy.imagelabeler.utils.shared.SharedPrefsKeys
import com.reedy.imagelabeler.view.image.BoxUpdatedListener
import kotlinx.android.synthetic.main.fragment_annotations.*
import kotlinx.android.synthetic.main.label_text_cell.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.FileOutputStream
import java.io.FileWriter
import java.nio.charset.Charset
import java.util.*

class AnnotationsFragment:
    BaseFragment<AnnotationsViewState, AnnotationsViewEvent, AnnotationsViewEffect, AnnotationsViewModel>
        (R.layout.fragment_annotations), BoxUpdatedListener, KoinComponent
{
    companion object {
        private const val TAG = "Annotations"
        private const val REQUEST_CODE = 56572
        private const val CREATE_FILE = 4201
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

    private val labelAdapter by lazy {
        LabelAdapter().apply {
            onClick = {
                viewModel.process(AnnotationsViewEvent.LabelClicked(it))
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
        //export.setOnClickListener { viewModel.process(AnnotationsViewEvent.ExportFiles) }
        refresh.setOnClickListener { viewModel.process(AnnotationsViewEvent.RefreshDirectory) }
        undo.setOnClickListener { viewModel.process(AnnotationsViewEvent.OnUndo) }
        redo.setOnClickListener { viewModel.process(AnnotationsViewEvent.OnRedo) }
        //slide_to_close.setOnClickListener { viewModel.process(AnnotationsViewEvent.ChangeDirectoryPanelState) }
        clear.setOnClickListener { showDialog() }
        add_label.setOnClickListener { viewModel.process(AnnotationsViewEvent.AddNewLabel(UiLabel(name ="test", group = "group", labelNumber = 1))) }
        directory_recycler.adapter = adapter
        label_recycler.adapter = labelAdapter
        askPermission()

    }

    override fun renderState(viewState: AnnotationsViewState) {
        Log.i(TAG, "renderState: Rendering")
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
        /*if (viewState.directoryTreeIsOpen) {
            val slide: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_open)
            project_structure.visibility = View.VISIBLE
            project_structure.startAnimation(slide)
            chevron.setImageResource(R.drawable.ic_baseline_chevron_left_24)
        } else {
            val slide: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_close)
            project_structure.visibility = View.GONE
            project_structure.startAnimation(slide)
            chevron.setImageResource(R.drawable.ic_baseline_chevron_right_24)
        }*/
        val boxesSafe = viewState.imageData?.boxes ?: return
        image_editor.updateBoxList(boxesSafe)
        adapter.submitList(viewState.directory)
        labelAdapter.submitList(viewState.labelList)
        title.text = viewState.directoryName
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
            is AnnotationsViewEffect.ShowToast -> {
                Toast.makeText(requireContext(), effect.msg, Toast.LENGTH_SHORT).show()
            }
            is AnnotationsViewEffect.OnSaveTFObjectCsv -> { saveTFObjCsv(effect.csvString) }
        }
    }

    private fun saveTFObjCsv(csvString: String) {
        try {
            val permission = requireActivity().contentResolver.persistedUriPermissions[0]
            val dir = DocumentFile.fromTreeUri(requireContext(), permission.uri)
            val file = dir?.createFile("plain/text","annotations.csv") ?: return
            val outputStream = requireActivity().contentResolver.openOutputStream(file.uri) ?: return
            outputStream.write(csvString.toByteArray(Charset.forName("UTF-8")))
            outputStream.flush()
            outputStream.close()
        } catch (e: Exception) {
            Log.e(TAG, "writeTensorflowObjectDetectionCsv: ${e.localizedMessage}")
        }
    }

    private fun enableZoom(bool: Boolean) {
        image_editor.setOverScrollHorizontal(bool)
        image_editor.setOverScrollVertical(bool)
        image_editor.setScrollEnabled(bool)
        image_editor.setHorizontalPanEnabled(bool)
        image_editor.setVerticalPanEnabled(bool)
        image_editor.isEditingEnabled(!bool)

    }

    private fun initDir(isFirst: Boolean = false) {
        val uri = sharedProvider.getSharedPrefs(SharedPrefsKeys.DIR_URI)?.toUri() ?: return
        val dir = DocumentFile.fromTreeUri(requireContext(), uri)
        val files = dir?.listFiles() ?: return
        val name = dir.name ?: return
        viewModel.process(AnnotationsViewEvent.UpdateDirectory(files.toMutableList(), name, isFirst))
    }

    private fun exportAnnotations(exportState: ExportState) {

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

    private fun showDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.confirm_clear_title)
            .setMessage(R.string.confirm_clear_desc)
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.accept) { dialog, _ ->
                viewModel.process(AnnotationsViewEvent.OnClear)
                dialog.dismiss()
            }.show()
    }

    private fun askPermission() {
        val uri = sharedProvider.getSharedPrefs(SharedPrefsKeys.DIR_URI)?.toUri()
        if (uri == null || !uriValid(uri)) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            startActivityForResult(intent, REQUEST_CODE)
        } else {
            Log.i(TAG, "askPermission: uri=$uri")
            initDir(true)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            val uri = data?.data
            if (uri != null) {
                var flags = data.flags
                flags = flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                //this is the uri user has provided us
                sharedProvider.saveToSharedPrefs(SharedPrefsKeys.DIR_URI, uri.toString())
                requireActivity().contentResolver.takePersistableUriPermission(uri, flags)

                initDir(true)
            }
        } /*else if (resultCode == RESULT_OK && requestCode == CREATE_FILE) {
            val uri = data?.data
            val writer = FileWriter(uri?.path.toString())
            try {
                writer.append(csvString)
            } catch (e: Exception) {
                Log.e(TAG, "writeTensorflowObjectDetectionCsv: ${e.localizedMessage}")
            } finally {
                writer.flush()
                writer.close()
            }

        }*/
    }

    private fun uriValid(uri: Uri): Boolean {
        requireActivity().contentResolver.persistedUriPermissions.forEach {
            if (it.uri == uri && it.isReadPermission && it.isWritePermission && it.persistedTime <= Date().time) {
                return true
            }
        }
        return false
    }

    override fun onBoxAdded(box: Box, onlyVisual: Boolean) {
        viewModel.process(AnnotationsViewEvent.OnBoxAdded(box, onlyVisual))
    }

    override fun onStop() {
        viewModel.process(AnnotationsViewEvent.OnStop)
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.export -> {
                viewModel.process(AnnotationsViewEvent.OnExportAnnotations(ExportState.TF_OBJECT_CSV))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
}