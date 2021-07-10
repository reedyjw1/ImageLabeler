package com.reedy.imagelabeler.view.overlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import androidx.core.view.children
import com.github.chrisbanes.photoview.PhotoView
import kotlin.math.abs


class Overlay(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs){

    var boxWidth: Int = 2
    var boxHeight: Int = 2
    private var paint: Paint = Paint()
    private var isScaling = false
    private var wasJustScaling = false
    private var boxListener: BoxAdded? = null
    private var down = false
    private var box:Box? = null

    init {
        paint.color = Color.YELLOW
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 12.0f
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeMiter = 100.0f
    }
    
    private val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            isScaling = true
            wasJustScaling = true
            invalidate()
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector?) {
            isScaling = false
            super.onScaleEnd(detector)
        }

    }
    private val gestureDetector = ScaleGestureDetector(context, listener)
    var boxes = mutableListOf<Box>()

    companion object {
        private const val TAG = "OverlayView"
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)



        boxes.forEach { box ->
            val xMin = box.xMin ?: return
            val xMax = box.xMax ?: return
            val yMin = box.yMin ?: return
            val yMax = box.yMax ?: return

            canvas?.let {
                Log.i(TAG, "onDraw: ")
                it.drawRect(xMin.toFloat(), yMax.toFloat(), xMax.toFloat(), yMin.toFloat(), paint)
            }
        }
    }
    
    fun setOnBoxAddedListener(mListener:BoxAdded ) {
        boxListener = mListener
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(ev)
        return if (!isScaling) {
            val doReturn = handleTouchEvent(ev)
            if (doReturn) {
                super.dispatchTouchEvent(ev)
            } else {
                false
            }
        } else {
            super.dispatchTouchEvent(ev)
        }
    }

    private fun handleTouchEvent(ev: MotionEvent?): Boolean {
        when(ev?.action) {
            MotionEvent.ACTION_UP -> {
                if (!wasJustScaling) {
                    wasJustScaling = false
                    val x = ev.x
                    val y = ev.y

                    this.children.forEach {
                        if (it is PhotoView) {
                            val coords = floatArrayOf(x, y)
                            val matrix = Matrix()
                            it.imageMatrix.invert(matrix)
                            matrix.postTranslate(it.scrollX.toFloat(), it.scrollY.toFloat())
                            matrix.mapPoints(coords)
                            box?.xMax = coords[0].toInt()
                            box?.yMax = coords[1].toInt()
                            Log.i(TAG, "before width - handleTouchEvent: $box")
                            val xMax = box?.xMax ?: return true
                            val xMin = box?.xMin ?: return true
                            val yMax = box?.yMax ?: return true
                            val yMin = box?.yMin ?: return true

                            box?.width = abs(xMax - xMin)
                            box?.height = abs(yMax - yMin)

                            val notNullBox = box ?: return true
                            boxListener?.onBoxAdded(notNullBox)
                        } else if (it is TestingDraw) {
                            box?.let { box ->
                                it.boxes.add(box)
                                it.invalidate()
                            }
                        }
                    }
                } else {
                    box = null
                }

                return true
            }
            MotionEvent.ACTION_DOWN -> {
                box = Box()
                down = true
                val x = ev.x
                val y = ev.y

                this.children.forEach {
                    if (it is PhotoView) { 
                        val coords = floatArrayOf(x, y)
                        val matrix = Matrix()
                        it.imageMatrix.invert(matrix)
                        matrix.postTranslate(it.scrollX.toFloat(), it.scrollY.toFloat())
                        matrix.mapPoints(coords)
                        box?.xMin = coords[0].toInt()
                        box?.yMin = coords[1].toInt()
                    }
                }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                return false
            }
        }
        return true
    }


}