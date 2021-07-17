package com.reedy.imagelabeler.view.image

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.core.view.children
import com.otaliastudios.zoom.ZoomLayout
import com.reedy.imagelabeler.extensions.replace
import com.reedy.imagelabeler.model.Box

class PhotoViewEditor(context: Context, attrs: AttributeSet): ZoomLayout(context, attrs){
    
    companion object {
        const val TAG = "Test"
    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }

    fun isEditingEnabled(bool: Boolean) {
        children.firstOrNull()?.let {
            val overlay = it as Overlay
            overlay.isEditing = bool
        }
    }

    fun updateBoxes(box: Box) {
        children.firstOrNull()?.let {
            val overlay = it as Overlay
            val original = overlay.boxes.find { original -> original.uid == box.uid }
            if (original == null) {
                overlay.boxes.add(box)
            } else {
                val newList = overlay.boxes.replace(box) {ogBox -> ogBox.uid == box.uid}
                overlay.boxes = newList.toMutableList()
                overlay.invalidate()
            }
        }
    }

    fun updateBoxList(boxes: List<Box>) {
        children.firstOrNull()?.let {
            val overlay = it as Overlay
            overlay.updateBoxList(boxes.toMutableList())
            overlay.invalidate()
        }
    }

    fun addBoxListener(listener: BoxUpdatedListener) {
        children.firstOrNull()?.let {
            val overlay = it as Overlay
            overlay.setListener(listener)
        }
    }
}