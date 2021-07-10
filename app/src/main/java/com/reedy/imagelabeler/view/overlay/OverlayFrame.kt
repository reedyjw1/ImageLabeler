package com.reedy.imagelabeler.view.overlay

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
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
    private var paint: Paint = Paint()

    var boxClicked: ((Box) -> Unit)? = null

    init {
        paint.color = Color.YELLOW
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 12.0f
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeMiter = 100.0f
    }
    
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
            this@OverlayFrame.children.forEach {
                if (it is Overlay) {
                    boxes.forEach {

                    }
                }
            }
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

    private fun handleTouchEvent(ev: MotionEvent?): Boolean {
        when(ev?.action) {
            MotionEvent.ACTION_UP -> {
                if (!wasJustScaling) {
                    wasJustScaling = false
                    val touchX = ev.x.toInt()
                    val touchY = ev.y.toInt()

                    this.children.forEach {
                        if (it is Overlay) {
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
                            box?.scale = it.scale

                            val notNullBox = box ?: return true
                            boxListener?.onBoxAdded(notNullBox)
                            box?.let { box ->
                                Log.i(TAG, "handleTouchEvent: box=$box")
                                it.boxes.add(box)
                                it.invalidate()
                                /*val bitmap: Bitmap = Bitmap.createBitmap((it.drawable as BitmapDrawable).bitmap)
                                val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                                val canvas = Canvas(mutableBitmap)
                                Log.i(TAG, "handleTouchEvent: box=$box")
                                canvas.drawRect(
                                    xMin,
                                    yMax,
                                    xMax,
                                    yMin,
                                    paint
                                )
                                canvas.save()
                                it.setImageBitmap(mutableBitmap)

                                it.invalidate()*/
                            }
                        } /*else if (it is Overlay) {
                            box?.let { box ->
                                Log.i(TAG, "handleTouchEvent: box=$box")
                                it.boxes.add(box)
                                it.invalidate()
                            }
                        }*/
                    }
                } else {
                    box = null
                }

                return true
            }
            MotionEvent.ACTION_DOWN -> {
                box = Box()
                down = true
                val touchX = ev.x
                val touchY = ev.y

                this.children.forEach {
                    if (it is Overlay) {
                        val values = FloatArray(9)
                        matrix.getValues(values)


                        val relativeX: Float = (touchX - values[2]) / values[0]
                        val relativeY: Float = (touchY - values[5]) / values[4]
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