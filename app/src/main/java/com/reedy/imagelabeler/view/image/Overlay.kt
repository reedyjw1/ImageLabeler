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
import androidx.appcompat.widget.AppCompatImageView
import com.reedy.imagelabeler.model.Box
import java.util.*
import kotlin.math.abs

class Overlay(context: Context, attrs: AttributeSet): AppCompatImageView(context, attrs) {

    var boxes = mutableListOf<Box>()
    private var paint: Paint = Paint()
    private var box: Box = Box()
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
        Log.i(TAG, "onDraw: inOverlay - ${boxes.size}")
        val bitmapWidth = (drawable as? BitmapDrawable)?.bitmap?.width?.toFloat() ?: return
        val bitmapHeight = (drawable as? BitmapDrawable)?.bitmap?.height?.toFloat() ?: return
        val xScale = bitmapWidth / width.toFloat()
        val yScale = bitmapHeight / height.toFloat()
        boxes.forEach { box ->
            val xMin = box.xMin / xScale
            val xMax = box.xMax / xScale
            val yMin = box.yMin / yScale
            val yMax = box.yMax / yScale

            canvas?.drawRect(xMin, yMax, xMax, yMin, paint)
        }
    }

    fun updateBoxList(newList: MutableList<Box>) {
        boxes.clear()
        newList.forEach {
            boxes.add(it)
        }
    }

    fun setListener(listener: BoxUpdatedListener) {
        boxListener = listener
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val xScale = (drawable as BitmapDrawable).bitmap.width.toFloat() / width.toFloat()
        val yScale = (drawable as BitmapDrawable).bitmap.height.toFloat() / height.toFloat()
        val touchX = event?.x ?: return true
        val touchY = event.y

        if (!isEditing) return false

        when(event.action) {
            MotionEvent.ACTION_UP -> {
                Log.i(TAG, "onTouchEvent: up")
                box.xMax = (touchX * xScale)
                box.yMax = (touchY * yScale)

                box.width = abs(box.xMax - box.xMin).toInt()
                box.height = abs(box.yMax - box.yMin).toInt()

                boxListener?.onBoxAdded(box, false)
                invalidate()
                box = Box()
            }
            MotionEvent.ACTION_DOWN -> {
                box = Box()
                box.xMin = touchX
                box.yMin = touchY
            }
            MotionEvent.ACTION_MOVE -> {
                box.xMax = (touchX * xScale)
                box.yMax = (touchY * yScale)
                box.xScale = xScale
                box.yScale = yScale

                box.width = abs(box.xMax - box.xMin).toInt()
                box.height = abs(box.yMax - box.yMin).toInt()

                boxListener?.onBoxAdded(box, true)
                invalidate()
            }
        }
        return true
    }
}