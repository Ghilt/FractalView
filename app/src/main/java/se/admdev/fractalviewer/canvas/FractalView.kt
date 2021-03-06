package se.admdev.fractalviewer.canvas

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View

// TODO experimental drawing class
class FractalView(context: Context, attrs: AttributeSet) : View(context, attrs) {

//    Rejected from experimentation on what is heaviest to draw
//    val list: MutableList<RectF> = mutableListOf()

    private var active = true
    private var paths: MutableList<Path> = mutableListOf()

    private val paint: Paint
    private var scaleFactor = 1f
    private var midPointX: Float = 0f
    private var midPointY: Float = 0f
    private var quarterPointY: Float = 0f

    // if infinity zoom is to be applied I think scaling the path is necessary
    private val scaleGestureDetector = ScaleGestureDetector(getContext(), PinchListener())
    private val touchData = TouchInteractionHandler(this)
    val iterationCount
        get() = paths.size

    //http://android-developers.blogspot.com/2010/06/making-sense-of-multitouch.html

    init {
//        setBackgroundColor(Color.parseColor("#330F445F"))

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
            quarterPointY = midPointY / 2
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()

        canvas.scale(scaleFactor, scaleFactor, midPointX, midPointY)
        canvas.translate(
            midPointX + touchData.posX / scaleFactor,
            quarterPointY + touchData.posY / scaleFactor
        ) // scale translation inversely to maintain reasonable panning distance

        paths.forEach { path -> canvas.drawPath(path, paint) }

        canvas.restore()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        scaleGestureDetector.onTouchEvent(ev)
        touchData.onTouchEvent(ev, scaleGestureDetector.isInProgress)
        return true
    }

//    fun addRectTemp(iterationAsRectangles: List<RectF>) {
//        list.addAll(iterationAsRectangles)
//    }

// When I had one giant path for entire fractal which lagged more than splitting it to one path per iteration
//    fun addPathUpdate(pathUpdate: (List<Path>) -> Unit) {
//        pathUpdate.invoke(paths)
//    }

    fun addPaths(newIterationPath: Path) {
        if (active){
            paths.add(newIterationPath)
        }
    }

    fun resetAndDisable() {
        paths = mutableListOf()
        active = false
        invalidate()
    }

    fun activate() {
        active = true
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