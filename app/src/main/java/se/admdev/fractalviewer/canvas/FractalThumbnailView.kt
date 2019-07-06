package se.admdev.fractalviewer.canvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import se.admdev.fractalviewer.R
import se.admdev.fractalviewer.canvas.CellularFractalArtist.Companion.CELL_SIZE
import kotlin.math.min

/* Trimmed down version of the fractal view, slight case of duplicated code for the time being*/
class FractalThumbnailView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    var iterations = 0

    private var paths: MutableList<Path> = mutableListOf()

    private val paint: Paint

    init {
        val strokeWidth = 2

        paint = Paint()
        paint.isAntiAlias = false // still get blurry when zooming
        paint.style = Paint.Style.FILL
        paint.alpha = 255
        paint.strokeWidth = strokeWidth.toFloat()
        paint.color = Color.BLACK

        context.theme.obtainStyledAttributes(attrs, R.styleable.FractalThumbnailView, 0, 0).apply {
            try {
                iterations = getInteger(R.styleable.FractalThumbnailView_iterations, 0)
            } finally {
                recycle()
            }
        }
    }

    // https://stackoverflow.com/questions/13273838/onmeasure-wrap-content-how-do-i-know-the-size-to-wrap
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 2 * CELL_SIZE.toInt() * iterations + paddingStart + paddingEnd
        val desiredHeight = CELL_SIZE.toInt() * iterations + paddingTop + paddingBottom

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> min(desiredWidth, widthSize)
            else -> desiredWidth
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> min(desiredHeight, heightSize)
            else -> desiredHeight
        }

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val midPointX = (width / 2).toFloat()
        canvas.save()
        canvas.translate(midPointX, paddingTop.toFloat())
        paths.forEach { path -> canvas.drawPath(path, paint) }
        canvas.restore()
    }

    private fun addPaths(newIterationPaths: Path) {
        paths.add(newIterationPaths)
    }

    private fun clearData() {
        paths.clear()
    }

    fun setFractalData(miniatureData: List<Path>?) {
        iterations = miniatureData?.size ?: 0
        clearData()
        miniatureData?.apply {
            forEach { addPaths(it) }
        }
        requestLayout()
        invalidate()
    }
}