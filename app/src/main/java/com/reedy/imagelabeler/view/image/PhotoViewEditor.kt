package com.reedy.imagelabeler.view.image

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.view.children
import com.otaliastudios.zoom.ZoomLayout

class PhotoViewEditor(context: Context, attrs: AttributeSet): ZoomLayout(context, attrs) {

    private var box: Box? = null
    
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
}