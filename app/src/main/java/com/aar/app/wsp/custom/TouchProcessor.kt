package com.aar.app.wsp.custom

import android.view.MotionEvent
import kotlin.math.abs
import kotlin.math.max

/**
 * Created by abdularis on 15/06/17.
 *
 * Digunakan untuk memproses data dari MouseEvent agar lebih mudah digunakan,
 * memiliki movement threshold untuk mengubah sensitivitas perpindahan sentuhan
 */
internal class TouchProcessor(
    private val mListener: OnTouchProcessed, moveThreshold: Float
) {
    private var isDown = false
    private val moveThreshold: Float = max(moveThreshold, 0.1f)
    private var lastX = 0f
    private var lastY = 0f

    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
                lastY = event.y
                isDown = true
                mListener.onDown(event)
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                isDown = false
                mListener.onUp(event)
            }
            MotionEvent.ACTION_MOVE -> if (isDown &&
                (abs(lastX - event.x) > moveThreshold || abs(lastY - event.y) > moveThreshold)) {
                lastX = event.x
                lastY = event.y
                mListener.onMove(event)
            }
            else -> return false
        }
        return true
    }

    internal interface OnTouchProcessed {
        fun onDown(event: MotionEvent?)
        fun onUp(event: MotionEvent?)
        fun onMove(event: MotionEvent?)
    }

}