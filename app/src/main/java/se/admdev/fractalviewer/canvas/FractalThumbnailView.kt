package se.admdev.fractalviewer.canvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

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

    // TODO https://stackoverflow.com/questions/13273838/onmeasure-wrap-content-how-do-i-know-the-size-to-wrap
    // make it so wrap content wraps the entire thumbnail

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.translate(midPointX, 0f)
        paths.forEach { it.forEachIndexed { i, path -> canvas.drawPath(path, paints[i]) } }
        canvas.restore()
    }

    fun addPaths(newIterationPaths: List<Path>) {
        paths.add(newIterationPaths)
    }

    fun clearData() {
        paths.clear()
    }
}