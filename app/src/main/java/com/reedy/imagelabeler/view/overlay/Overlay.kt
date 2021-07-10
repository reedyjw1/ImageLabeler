package com.reedy.imagelabeler.view.overlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.reedy.imagelabeler.view.overlay.model.Box

class Overlay(context: Context, attrs: AttributeSet): View(context, attrs) {

    var boxes = mutableListOf<Box>()
    private var paint: Paint = Paint()

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
}