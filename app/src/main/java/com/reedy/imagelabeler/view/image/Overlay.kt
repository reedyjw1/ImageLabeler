package com.reedy.imagelabeler.view.image

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView
import kotlin.math.abs

class Overlay(context: Context, attrs: AttributeSet): ImageView(context, attrs) {

    var boxes = mutableListOf<Box>()
    private var paint: Paint = Paint()
    private var box: Box? = null
    var isEditing = false
    
    companion object {
        private const val TAG = "OverlayView"
    }

    init {
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 8.0f
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeMiter = 100.0f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        boxes.forEach { box ->
            val xMin = box.xMin ?: return
            val xMax = box.xMax ?: return
            val yMin = box.yMin ?: return
            val yMax = box.yMax ?: return

            canvas?.let {
                it.drawRect(xMin, yMax, xMax, yMin, paint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x ?: return true
        val touchY = event.y

        if (!isEditing) return false

        when(event.action) {
            MotionEvent.ACTION_UP -> {
                Log.i(TAG, "onTouchEvent: up")
                box?.xMax = touchX
                box?.yMax = touchY
                box?.relativeToBitmapXMax = touchX/2f
                box?.relativeToBitmapYMax = touchY/2f

                Log.i(TAG, "before width - handleTouchEvent: $box")
                val x2 = box?.relativeToBitmapXMax ?: return true
                val x1 = box?.relativeToBitmapXMin ?: return true
                val y2 = box?.relativeToBitmapYMax ?: return true
                val y1 = box?.relativeToBitmapYMin?: return true

                box?.width = abs(x2 - x1).toInt()
                box?.height = abs(y2 - y1).toInt()
                Log.i(TAG, "handleTouchEvent: box=$box")
                val notNullBox = box ?: return true

                box?.let { box ->
                    Log.i(TAG, "handleTouchEvent: box=$box")
                    boxes.add(box)
                    invalidate()
                }
                box = null
            }
            MotionEvent.ACTION_DOWN -> {
                box = Box()
                box?.xMin = touchX
                box?.yMin = touchY
                box?.relativeToBitmapXMin = touchX/2f
                box?.relativeToBitmapYMin = touchY/2f
                Log.i(TAG, "handleTouchEvent: box=$box")
            }
            MotionEvent.ACTION_MOVE -> {
                box?.xMax = touchX
                box?.yMax = touchY
                box?.relativeToBitmapXMax = touchX/2f
                box?.relativeToBitmapYMax = touchY/2f

                Log.i(TAG, "before width - handleTouchEvent: $box")
                val xMax = box?.xMax ?: return true
                val xMin = box?.xMin ?: return true
                val yMax = box?.yMax ?: return true
                val yMin = box?.yMin ?: return true

                box?.width = abs(xMax -xMin).toInt()
                box?.height = abs(yMax - yMin).toInt()
                Log.i(TAG, "handleTouchEvent: box=$box")
                val notNullBox = box ?: return true

                box?.let { box ->
                    Log.i(TAG, "handleTouchEvent: box=$boxes")
                    boxes.add(box)
                    invalidate()
                }
            }
        }
        return true
    }
}