package se.admdev.fractalviewer.canvas

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View


class FractalView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    val list: MutableList<RectF> = mutableListOf()

    var path = Path()

    private val paint: Paint
    private var scaleFactor = 1f
    private var midPointX: Float = 0f
    private var midPointY: Float = 0f

    // if infinity zoom is to be applied I think scaling the path is necessary
    private val scaleGestureDetector = ScaleGestureDetector(getContext(), PinchListener())
    private val touchData = TouchInteractionHandler(this)

    //http://android-developers.blogspot.com/2010/06/making-sense-of-multitouch.html

    init {
        setBackgroundColor(Color.parseColor("#330F445F"))

        val strokeWidth = 2

        paint = Paint()
        paint.isAntiAlias = false // still get blurry when zooming
        paint.style = Paint.Style.FILL
        paint.alpha = 255
        paint.strokeWidth = strokeWidth.toFloat()
        paint.color = Color.BLACK


//        scaleMatrix.setScale(5f, 5f)
//        path.transform(scaleMatrix)

        post {
            midPointX = (width / 2).toFloat()
            midPointY = (height / 2).toFloat()
            path.moveTo(midPointX, midPointY)
//            path.addRect(50f, 50f, 100f, 100f, Path.Direction.CW)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()

//        Log.d("spx", "Scalefactor: $scaleFactor TouchX: ${touchData.posX} TouchY: ${touchData.posY} ")

        canvas.scale(scaleFactor, scaleFactor, midPointX, midPointY)
        canvas.translate(
            touchData.posX * 1 / scaleFactor,
            touchData.posY * 1 / scaleFactor
        ) // scale translation inversely to maintain reasonable panning distance
        canvas.drawPath(path, paint)

        list.forEach { canvas.drawRect(it, paint) }

        canvas.restore()
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        scaleGestureDetector.onTouchEvent(ev)
        touchData.onTouchEvent(ev, scaleGestureDetector.isInProgress)
        return true
    }

    fun addRectTemp(iterationAsRectangles: List<RectF>) {
        list.addAll(iterationAsRectangles)
    }

    private inner class PinchListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            detector?.let {
                scaleFactor *= detector.scaleFactor
                invalidate()
            }
            return true
        }
    }
}