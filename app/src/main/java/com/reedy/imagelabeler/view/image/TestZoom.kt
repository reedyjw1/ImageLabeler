package com.reedy.imagelabeler.view.image

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView
import androidx.core.view.children
import com.otaliastudios.zoom.ZoomLayout
import com.reedy.imagelabeler.view.iamge.Overlay
import kotlin.math.abs

class TestZoom(context: Context, attrs: AttributeSet): ZoomLayout(context, attrs) {

    private var box: Box? = null
    var isEditing = false
    
    companion object {
        const val TAG = "Test"
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }
    
    /*override fun onTouchEvent(ev: MotionEvent): Boolean {
        if(isEditing) {
            when(ev.action) {
                MotionEvent.ACTION_UP -> {
                    Log.i(TAG, "handleTouchEvent: up")
                    val touchX = ev.x
                    val touchY = ev.y

                    val values = FloatArray(9)
                    matrix.getValues(values)
                    val relativeX: Float = (touchX - values[2]) / values[0]
                    val relativeY: Float = (touchY - values[5]) / values[4]

                    if (children.firstOrNull() is OverlayFrame) {
                        val child = children.firstOrNull() as? OverlayFrame
                        child?.updateEnd(touchX, touchY)
                    }
                    return true
                }
                MotionEvent.ACTION_DOWN -> {
                    val touchX = ev.x
                    val touchY = ev.y

                    val values = FloatArray(9)
                    matrix.getValues(values)
                    val relativeX: Float = (touchX - values[2]) / values[0]
                    val relativeY: Float = (touchY - values[5]) / values[4]

                    if (children.firstOrNull() is OverlayFrame) {
                        val child = children.firstOrNull() as? OverlayFrame
                        child?.updateStart(touchX, touchY)
                    }
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    val touchX = ev.x
                    val touchY = ev.y

                    val values = FloatArray(9)
                    matrix.getValues(values)
                    val relativeX: Float = (touchX - values[2]) / values[0]
                    val relativeY: Float = (touchY - values[5]) / values[4]

                    if (children.firstOrNull() is OverlayFrame) {
                        val child = children.firstOrNull() as? OverlayFrame
                        child?.updateMove(touchX, touchY)
                    }
                    return false
                }
            }
        }
        
        return super.onTouchEvent(ev)
    }*/
}