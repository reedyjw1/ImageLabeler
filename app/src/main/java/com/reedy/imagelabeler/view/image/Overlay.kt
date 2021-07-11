package com.reedy.imagelabeler.view.iamge

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.core.view.children
import com.reedy.imagelabeler.view.image.Box
import com.reedy.imagelabeler.view.image.OverlayFrame
import kotlin.math.abs

class Overlay(context: Context, attrs: AttributeSet): ImageView(context, attrs) {

    var boxes = mutableListOf<Box>()
    private var paint: Paint = Paint()
    private var box: Box? = null
    
    companion object {
        private const val TAG = "OverlayView"
    }

    init {
        paint.color = Color.YELLOW
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 12.0f
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
                Log.i("TEST", "onDraw: ")
                it.drawRect(xMin.toFloat(), yMax.toFloat(), xMax.toFloat(), yMin.toFloat(), paint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x ?: return true
        val touchY = event.y

        when(event.action) {
            MotionEvent.ACTION_UP -> {
                Log.i(TAG, "onTouchEvent: up")
                val values = FloatArray(9)
                matrix.getValues(values)
                val relativeX: Float = (touchX - values[2]) / values[0]
                val relativeY: Float = (touchY - values[5]) / values[4]

                box?.xMax = touchX
                box?.yMax = touchY
                box?.relativeToBitmapXMax = relativeX
                box?.relativeToBitmapYMax = relativeY

                Log.i(TAG, "before width - handleTouchEvent: $box")
                val xMax = box?.xMax ?: return true
                val xMin = box?.xMin ?: return true
                val yMax = box?.yMax ?: return true
                val yMin = box?.yMin ?: return true
                var x1 = box?.relativeToBitmapXMax ?: return true
                var x2 = box?.relativeToBitmapXMin ?: return true
                var y1 = box?.relativeToBitmapYMax ?: return true
                var y2 = box?.relativeToBitmapYMin ?: return true

                box?.width = abs((x1 - x2) / 2).toInt()
                box?.height = abs((y1 - y2) / 2).toInt()
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
                Log.i(TAG, "onTouchEvent: down")
                box = Box()

                val values = FloatArray(9)
                matrix.getValues(values)
                val relativeX: Float = (touchX - values[2]) / values[0]
                val relativeY: Float = (touchY - values[5]) / values[4]

                box?.xMin = touchX
                box?.yMin = touchY
                box?.relativeToBitmapXMin = relativeX
                box?.relativeToBitmapYMin = relativeY
                Log.i(TAG, "handleTouchEvent: box=$box")
            }
            MotionEvent.ACTION_MOVE -> {
                Log.i(TAG, "onTouchEvent: move")
                Log.i(TAG, "handleTouchEvent: test")

                val values = FloatArray(9)
                matrix.getValues(values)
                val relativeX: Float = (touchX - values[2]) / values[0]
                val relativeY: Float = (touchY - values[5]) / values[4]

                box?.xMax = touchX
                box?.yMax = touchY
                box?.relativeToBitmapXMax = relativeX
                box?.relativeToBitmapYMax = relativeY

                Log.i(TAG, "before width - handleTouchEvent: $box")
                val xMax = box?.xMax ?: return true
                val xMin = box?.xMin ?: return true
                val yMax = box?.yMax ?: return true
                val yMin = box?.yMin ?: return true
                var x1 = box?.relativeToBitmapXMax ?: return true
                var x2 = box?.relativeToBitmapXMin ?: return true
                var y1 = box?.relativeToBitmapYMax ?: return true
                var y2 = box?.relativeToBitmapYMin ?: return true

                box?.width = abs((x1 - x2) / 2).toInt()
                box?.height = abs((y1 - y2) / 2).toInt()
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

    fun updateStart(x: Float, y: Float) {
        box = Box()

        val values = FloatArray(9)
        matrix.getValues(values)
        val relativeX: Float = (x - values[2]) / values[0]
        val relativeY: Float = (y - values[5]) / values[4]

        box?.xMin = relativeX
        box?.yMin = relativeY
        Log.i(TAG, "handleTouchEvent: box=$box")
    }

    fun updateMove(x: Float, y: Float) {
        Log.i(TAG, "handleTouchEvent: test")

        val values = FloatArray(9)
        matrix.getValues(values)
        val relativeX: Float = (x - values[2]) / values[0]
        val relativeY: Float = (y - values[5]) / values[4]

        box?.xMax = relativeX
        box?.yMax = relativeY

        Log.i(TAG, "before width - handleTouchEvent: $box")
        val xMax = box?.xMax ?: return
        val xMin = box?.xMin ?: return
        val yMax = box?.yMax ?: return
        val yMin = box?.yMin ?: return

        box?.width = abs(xMax - xMin).toInt()
        box?.height = abs(yMax - yMin).toInt()
        Log.i(TAG, "handleTouchEvent: box=$box")
        val notNullBox = box ?: return

        box?.let { box ->
            Log.i(TAG, "handleTouchEvent: box=$box")
            boxes.add(box)
            invalidate()
        }
    }

    fun updateEnd(x: Float, y: Float) {
        val values = FloatArray(9)
        matrix.getValues(values)
        val relativeX: Float = (x - values[2]) / values[0]
        val relativeY: Float = (y - values[5]) / values[4]

        box?.xMax = relativeX
        box?.yMax = relativeY

        Log.i(TAG, "before width - handleTouchEvent: $box")
        val xMax = box?.xMax ?: return
        val xMin = box?.xMin ?: return
        val yMax = box?.yMax ?: return
        val yMin = box?.yMin ?: return

        box?.width = abs(xMax - xMin).toInt()
        box?.height = abs(yMax - yMin).toInt()
        Log.i(TAG, "handleTouchEvent: box=$box")
        val notNullBox = box ?: return

        box?.let { box ->
            Log.i(TAG, "handleTouchEvent: box=$box")
            boxes.add(box)
            invalidate()
        }
        box = null
    }
}