package se.admdev.fractalviewer.canvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import se.admdev.fractalviewer.canvas.CellularFractalArtist.Companion.CELL_SIZE

/* Trimmed down version of the fractal view, slight case of duplicated code for the time being*/
class FractalThumbnailView(context: Context, attrs: AttributeSet) : View(context, attrs) {

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
    private var midPointX: Float = 0f

    init {
        val strokeWidth = 2

        paint = Paint()
        paint.isAntiAlias = false // still get blurry when zooming
        paint.style = Paint.Style.FILL
        paint.alpha = 255
        paint.strokeWidth = strokeWidth.toFloat()
        paint.color = Color.BLACK

        post {
            midPointX = (width / 2).toFloat()
            invalidate()
        }
    }
    // https://stackoverflow.com/questions/13273838/onmeasure-wrap-content-how-do-i-know-the-size-to-wrap
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 2 * CELL_SIZE.toInt() * ITERATIONS_OF_THUMBNAIL
        val desiredHeight = CELL_SIZE.toInt() * ITERATIONS_OF_THUMBNAIL

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> Math.min(desiredWidth, widthSize)
            else -> desiredWidth
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> Math.min(desiredHeight, heightSize)
            else -> desiredHeight
        }

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.translate(midPointX, 0f)
        paths.forEach { it.forEachIndexed { i, path -> canvas.drawPath(path, paints[i]) } }
        canvas.restore()
    }

    private fun addPaths(newIterationPaths: List<Path>) {
        paths.add(newIterationPaths)
    }

    private fun clearData() {
        paths.clear()
    }

    fun setFractalData(miniatureData: List<List<Path>>?) {
        clearData()
        miniatureData?.apply {
            forEach { addPaths(it) }
        }
        requestLayout()
        invalidate()
    }

    companion object {
        const val ITERATIONS_OF_THUMBNAIL = 10
    }
}