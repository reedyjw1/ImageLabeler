package com.reedy.imagelabeler.view.image

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.children
import com.reedy.imagelabeler.view.iamge.Overlay
import kotlin.math.abs


class OverlayFrame(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs){

    var boxWidth: Int = 2
    var boxHeight: Int = 2
    private var isScaling = false
    private var wasJustScaling = false
    private var down = false
    private var box: Box? = null

    var boxClicked: ((Box) -> Unit)? = null

    private val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            isScaling = true
            wasJustScaling = true
            invalidate()
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector?) {
            Log.i(TAG, "onScaleEnd: ")
            isScaling = false
            super.onScaleEnd(detector)
        }

        override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
            Log.i(TAG, "onScaleBegin: ")
            return super.onScaleBegin(detector)
        }

    }

    private val generalTouchListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            Log.i(TAG, "onSingleTapConfirmed: tap")
            return super.onSingleTapConfirmed(e)
        }

        //over
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }

    private val gestureDetector = ScaleGestureDetector(context, listener)
    private val tapListener = GestureDetector(context, generalTouchListener)
    var boxes = mutableListOf<Box>()

    companion object {
        private const val TAG = "OverlayView"
    }

    /*fun updateEnd(x: Float, y: Float) {
        val values = FloatArray(9)
        matrix.getValues(values)
        val relativeX: Float = (x - values[2]) / values[0]
        val relativeY: Float = (y - values[5]) / values[4]

        children.forEach {
            if (it is Overlay) {
                it.updateEnd(x, y)
            }
        }
    }

    fun updateStart(x: Float, y: Float) {
        val values = FloatArray(9)
        matrix.getValues(values)
        val relativeX: Float = (x - values[2]) / values[0]
        val relativeY: Float = (y - values[5]) / values[4]

        children.forEach {
            if (it is Overlay) {
                it.updateStart(x, y)
            }
        }
    }

    fun updateMove(x: Float, y: Float) {
        val values = FloatArray(9)
        matrix.getValues(values)
        val relativeX: Float = (x - values[2]) / values[0]
        val relativeY: Float = (y - values[5]) / values[4]

        children.forEach {
            if (it is Overlay) {
                it.updateMove(x, y)
            }
        }
    }*/


    /*override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(ev)
        //tapListener.onTouchEvent(ev)
        return if (!isScaling) {
            //val doReturn = handleTouchEvent(ev)
            if (doReturn) {
                super.dispatchTouchEvent(ev)
            } else {
                false
            }
        } else {
            super.dispatchTouchEvent(ev)
        }
    }*/



    /*private fun handleTouchEvent(ev: MotionEvent?): Boolean {
        Log.i(TAG, "handleTouchEvent: touched=${ev?.action}")
        when(ev?.action) {
            MotionEvent.ACTION_UP -> {
                if (!wasJustScaling) {
                    wasJustScaling = false
                    Log.i(TAG, "handleTouchEvent: up")
                    val touchX = ev.x.toInt()
                    val touchY = ev.y.toInt()

                    this.children.forEach {
                        Log.i(TAG, "handleTouchEvent: test")
                        if (it is ImageView) {
                            Log.i(TAG, "handleTouchEvent: imageView")
                            val values = FloatArray(9)
                            matrix.getValues(values)
                            val relativeX: Float = (touchX - values[2]) / values[0]
                            val relativeY: Float = (touchY - values[5]) / values[4]
                            box?.xMax = relativeX
                            box?.yMax = relativeY

                            Log.i(TAG, "before width - handleTouchEvent: $box")
                            val xMax = box?.xMax ?: return true
                            val xMin = box?.xMin ?: return true
                            val yMax = box?.yMax ?: return true
                            val yMin = box?.yMin ?: return true

                            box?.width = abs(xMax - xMin).toInt()
                            box?.height = abs(yMax - yMin).toInt()
                            Log.i(TAG, "handleTouchEvent: box=$box")
                            val notNullBox = box ?: return true

                        } else if (it is Overlay) {
                            box?.let { box ->
                                Log.i(TAG, "handleTouchEvent: box=$box")
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
                    if (it is ImageView) {

                        val values = FloatArray(9)
                        matrix.getValues(values)
                        val relativeX: Float = (x - values[2]) / values[0]
                        val relativeY: Float = (y - values[5]) / values[4]
                        box?.xMin = relativeX
                        box?.yMin = relativeY
                        Log.i(TAG, "handleTouchEvent: box=$box")
                    }
                }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                *//*val touchX = ev.x.toInt()
                val touchY = ev.y.toInt()

                this.children.forEach {
                    Log.i(TAG, "handleTouchEvent: test")
                    if (it is ImageView) {
                        Log.i(TAG, "handleTouchEvent: imageView")
                        val values = FloatArray(9)
                        matrix.getValues(values)
                        val relativeX: Float = (touchX - values[2]) / values[0]
                        val relativeY: Float = (touchY - values[5]) / values[4]
                        box?.xMax = relativeX
                        box?.yMax = relativeY

                        Log.i(TAG, "before width - handleTouchEvent: $box")
                        val xMax = box?.xMax ?: return true
                        val xMin = box?.xMin ?: return true
                        val yMax = box?.yMax ?: return true
                        val yMin = box?.yMin ?: return true

                        box?.width = abs(xMax - xMin).toInt()
                        box?.height = abs(yMax - yMin).toInt()
                        Log.i(TAG, "handleTouchEvent: box=$box")
                        val notNullBox = box ?: return true

                    } else if (it is Overlay) {
                        box?.let { box ->
                            Log.i(TAG, "handleTouchEvent: box=$box")
                            it.boxes.add(box)
                            it.invalidate()
                        }
                    }
                }*//*
                return false
            }
        }
        return true
    }*/


}