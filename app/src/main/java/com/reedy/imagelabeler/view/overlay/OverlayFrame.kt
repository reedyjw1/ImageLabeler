package com.reedy.imagelabeler.view.overlay

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.core.view.children
import com.github.chrisbanes.photoview.PhotoView
import com.reedy.imagelabeler.view.overlay.model.Box
import com.reedy.imagelabeler.view.overlay.model.BoxAdded
import kotlin.math.abs


class OverlayFrame(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs){

    var boxWidth: Int = 2
    var boxHeight: Int = 2
    private var isScaling = false
    private var wasJustScaling = false
    private var boxListener: BoxAdded? = null
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

    private val gestureDetector = ScaleGestureDetector(context, listener)
    private val tapListener = GestureDetector(context, generalTouchListener)
    var boxes = mutableListOf<Box>()

    companion object {
        private const val TAG = "OverlayView"
    }
    
    fun setOnBoxAddedListener(mListener: BoxAdded) {
        boxListener = mListener
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(ev)
        //tapListener.onTouchEvent(ev)
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

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun handleTouchEvent(ev: MotionEvent?): Boolean {
        when(ev?.action) {
            MotionEvent.ACTION_UP -> {
                if (!wasJustScaling) {
                    wasJustScaling = false
                    val touchX = ev.x.toInt()
                    val touchY = ev.y.toInt()

                    this.children.forEach {
                        if (it is PhotoView) {
                            /*val coords = floatArrayOf(touchX.toFloat(), touchY.toFloat())
                            val matrix = Matrix()
                            it.imageMatrix.invert(matrix)
                            matrix.postTranslate(it.scrollX.toFloat(), it.scrollY.toFloat())
                            matrix.mapPoints(coords)
                            box?.xMax = coords[0]
                            box?.yMax = coords[1]*/

                            val values = FloatArray(9)
                            matrix.getValues(values)

                            // values[2] and values[5] are the x,y coordinates of the top left corner of the drawable image, regardless of the zoom factor.
                            // values[0] and values[4] are the zoom factors for the image's width and height respectively. If you zoom at the same factor, these should both be the same value.

                            // event is the touch event for MotionEvent.ACTION_UP

                            // values[2] and values[5] are the x,y coordinates of the top left corner of the drawable image, regardless of the zoom factor.
                            // values[0] and values[4] are the zoom factors for the image's width and height respectively. If you zoom at the same factor, these should both be the same value.

                            // event is the touch event for MotionEvent.ACTION_UP
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

                            val notNullBox = box ?: return true
                            boxListener?.onBoxAdded(notNullBox)
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
                    if (it is PhotoView) { 
                        /*val coords = floatArrayOf(x, y)
                        val matrix = Matrix()
                        it.imageMatrix.invert(matrix)
                        matrix.postTranslate(it.scrollX.toFloat(), it.scrollY.toFloat())
                        matrix.mapPoints(coords)
                        box?.xMin = coords[0]
                        box?.yMin = coords[1]
*/
                        val values = FloatArray(9)
                        matrix.getValues(values)

                        // values[2] and values[5] are the x,y coordinates of the top left corner of the drawable image, regardless of the zoom factor.
                        // values[0] and values[4] are the zoom factors for the image's width and height respectively. If you zoom at the same factor, these should both be the same value.

                        // event is the touch event for MotionEvent.ACTION_UP

                        // values[2] and values[5] are the x,y coordinates of the top left corner of the drawable image, regardless of the zoom factor.
                        // values[0] and values[4] are the zoom factors for the image's width and height respectively. If you zoom at the same factor, these should both be the same value.

                        // event is the touch event for MotionEvent.ACTION_UP
                        val relativeX: Float = (x - values[2]) / values[0]
                        val relativeY: Float = (y - values[5]) / values[4]
                        box?.xMin = relativeX
                        box?.yMin = relativeY
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