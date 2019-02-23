package se.admdev.fractalviewer

import android.view.MotionEvent
import android.view.View

// Copied from: https://android-developers.googleblog.com/2010/06/making-sense-of-multitouch.html
// Doesn't seem to be possible to do panning with GestureDetectors... which is weird?

class TouchInteractionHandler(val v: View) {


    private var lastTouchX: Float = 0f
    private var lastTouchY: Float = 0f
    private var activePointerId: Int = MotionEvent.INVALID_POINTER_ID
    internal var posX: Float = 0f
        private set
    internal var posY: Float = 0f
        private set

    fun onTouchEvent(ev: MotionEvent?, scalingInProgress: Boolean) {
        if (ev == null) {
            return
        }

        when (ev.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> actionDown(ev)
            MotionEvent.ACTION_MOVE -> actionMove(ev, scalingInProgress)
            MotionEvent.ACTION_UP -> activePointerId = MotionEvent.INVALID_POINTER_ID
            MotionEvent.ACTION_CANCEL -> activePointerId = MotionEvent.INVALID_POINTER_ID
            MotionEvent.ACTION_POINTER_UP -> actionUp(ev)
        }
    }

    private fun actionDown(ev: MotionEvent) {
        lastTouchX = ev.x
        lastTouchY = ev.y
        activePointerId = ev.getPointerId(0)
    }

    private fun actionMove(ev: MotionEvent, scalingInProgress: Boolean) {
        val pointerIndex = ev.findPointerIndex(activePointerId)
        val x = ev.getX(pointerIndex)
        val y = ev.getY(pointerIndex)

        if (!scalingInProgress) {
            val dx = x - lastTouchX
            val dy = y - lastTouchY

            posX += dx
            posY += dy

        }

        lastTouchX = x
        lastTouchY = y

        v.invalidate()
    }

    private fun actionUp(ev: MotionEvent) {
        val pointerIndex = ev.action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
        val pointerId = ev.getPointerId(pointerIndex)
        if (pointerId == activePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            lastTouchX = ev.getX(newPointerIndex)
            lastTouchY = ev.getY(newPointerIndex)
            activePointerId = ev.getPointerId(newPointerIndex)
        }
    }
}