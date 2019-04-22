package se.admdev.fractalviewer.canvas

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View

// TODO experimental drawing class
class FractalView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    val list: MutableList<RectF> = mutableListOf()

    private val tempSize = 5

    private var paths: MutableList<List<Path>> = mutableListOf()
    private var paints: List<Paint> = List(tempSize) { i ->
        Paint().apply {
            isAntiAlias = false // still get blurry when zooming
            style = Paint.Style.FILL
            alpha = 255
            strokeWidth = 2.toFloat()
            color = Color.argb(1f, i / tempSize.toFloat(), i / tempSize.toFloat(), i / tempSize.toFloat())
        }
    }

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
//            path.moveTo(midPointX, midPointY)
//            path.addRect(50f, 50f, 100f, 100f, Path.Direction.CW)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()

//        Log.d("spx", "Scalefactor: $scaleFactor TouchX: ${touchData.posX} TouchY: ${touchData.posY} ")

        canvas.scale(scaleFactor, scaleFactor, midPointX, midPointY)
        canvas.translate(
            midPointX + touchData.posX / scaleFactor,
            quarterPointY + touchData.posY / scaleFactor
        ) // scale translation inversely to maintain reasonable panning distance

//        path.addRect(100f, 100f, 300f, 300f, Path.Direction.CCW)

        paths.forEach { it.forEachIndexed { i, path -> canvas.drawPath(path, paints[i]) } }
//        canvas.drawPath(path, paint)

        list.forEach { canvas.drawRect(it, paint) }

        canvas.restore()
//        path.addRect(300f, 300f, 450f, 400f, Path.Direction.CCW)

    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        scaleGestureDetector.onTouchEvent(ev)
        touchData.onTouchEvent(ev, scaleGestureDetector.isInProgress)
        return true
    }

    fun addRectTemp(iterationAsRectangles: List<RectF>) {
        list.addAll(iterationAsRectangles)
    }

// When I had one giant path for entire fractal which lagged more than splitting it to one path per iteration
//    fun addPathUpdate(pathUpdate: (List<Path>) -> Unit) {
//        pathUpdate.invoke(paths)
//    }

    fun addPaths(newIterationPaths: List<Path>) {
        paths.add(newIterationPaths)
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