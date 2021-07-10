package com.reedy.imagelabeler.view.overlay

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.scaleMatrix
import com.github.chrisbanes.photoview.PhotoView
import com.reedy.imagelabeler.view.overlay.model.Box

class Overlay(context: Context, attrs: AttributeSet): PhotoView(context, attrs) {

    var boxes = mutableListOf<Box>()
    private var paint: Paint = Paint()
    private lateinit var bitmap: Bitmap

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
            val boxScale = box.scale ?: return


            canvas?.let {
                val transformedScale = (scale / boxScale)
                Log.i("Drawing", "onDraw: scale=$scale, transformedScale=$transformedScale")

                canvas.drawRect(
                    xMin,
                    yMax,
                    xMax,
                    yMin,
                    paint
                )
                canvas.save()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return false
    }
}