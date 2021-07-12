package com.reedy.imagelabeler.view.image

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView
import com.reedy.imagelabeler.model.Box
import kotlin.math.abs

class Overlay(context: Context, attrs: AttributeSet): ImageView(context, attrs) {

    var boxes = mutableListOf<Box>()
    private var paint: Paint = Paint()
    var photoViewWidth: Int = 1
    private var box: Box? = null
    var isEditing = false
    var boxListener : BoxUpdatedListener? = null

    companion object {
        private const val TAG = "OverlayView"
    }

    init {
        paint.color = Color.YELLOW
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

    fun setListener(listener: BoxUpdatedListener) {
        boxListener = listener
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val translationX = (width/2) - (drawable as BitmapDrawable).bitmap.width/2
        val xScale = (drawable as BitmapDrawable).bitmap.width.toFloat() / width.toFloat()
        val yScale = (drawable as BitmapDrawable).bitmap.height.toFloat() / height.toFloat()
        val translationY = (height/2) - (drawable as BitmapDrawable).bitmap.height/2
        val touchX = event?.x ?: return true
        val touchY = event.y

        if (!isEditing) return false

        when(event.action) {
            MotionEvent.ACTION_UP -> {
                Log.i(TAG, "onTouchEvent: up")
                box?.xMax = touchX
                box?.yMax = touchY
                box?.relativeToBitmapXMax = (touchX * xScale).toInt()
                box?.relativeToBitmapYMax = (touchY * yScale).toInt()

                Log.i(TAG, "before width - handleTouchEvent: $box")
                val x2 = box?.relativeToBitmapXMax ?: return true
                val x1 = box?.relativeToBitmapXMin ?: return true
                val y2 = box?.relativeToBitmapYMax ?: return true
                val y1 = box?.relativeToBitmapYMin?: return true

                box?.width = abs(x2 - x1)
                box?.height = abs(y2 - y1)
                Log.i(TAG, "handleTouchEvent: box=$box")
                val notNullBox = box ?: return true

                box?.let { box ->
                    Log.i(TAG, "handleTouchEvent: final box=$box, listener=$boxListener")
                    boxListener?.onBoxAdded(box, false)
                    //boxes.add(box)
                    invalidate()
                }
                box = null
            }
            MotionEvent.ACTION_DOWN -> {
                box = Box()
                box?.xMin = touchX
                box?.yMin = touchY

                val scale = width/photoViewWidth
                val centerOfOverlay = Pair(width/2, height/2)
                val centerOfPhoto = Pair((drawable as BitmapDrawable).bitmap.width/2, (drawable as BitmapDrawable).bitmap.height/2)
                Log.i(TAG, "onTouchEvent: centerOfOverlau=$centerOfOverlay, centerOfImage=$centerOfPhoto")

                box?.relativeToBitmapXMin = (touchX * xScale).toInt()
                box?.relativeToBitmapYMin = (touchY * yScale).toInt()
                Log.i(TAG, "handleTouchEvent: box=$box")
            }
            MotionEvent.ACTION_MOVE -> {
                box?.xMax = touchX
                box?.yMax = touchY
                box?.relativeToBitmapXMax = (touchX * xScale).toInt()
                box?.relativeToBitmapYMax = (touchY * yScale).toInt()

                Log.i(TAG, "before width - handleTouchEvent: $box")
                val xMax = box?.xMax ?: return true
                val xMin = box?.xMin ?: return true
                val yMax = box?.yMax ?: return true
                val yMin = box?.yMin ?: return true

                box?.width = abs(xMax -xMin).toInt()
                box?.height = abs(yMax - yMin).toInt()
                Log.i(TAG, "handleTouchEvent: box=$box")
                val notNullBox = box ?: return true

                box?.label = "box"
                box?.group = "test"
                box?.fileName = "grid.jpg"

                box?.let { box ->
                    Log.i(TAG, "handleTouchEvent:  box=$box")
                    boxListener?.onBoxAdded(box, true)
                    //boxes.add(box)
                    invalidate()
                }
            }
        }
        return true
    }
}