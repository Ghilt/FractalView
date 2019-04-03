package se.admdev.fractalviewer.canvas

import android.graphics.RectF
import se.admdev.fractalviewer.canvas.model.Cell

class CellularFractalArtist {

    private val cellSize = 10f

    fun getIterationAsRectangles(iteration: Int, cells: List<Cell>): List<RectF> {
        val top = cellSize * iteration
        val bottom = cellSize * (iteration + 1)
        return cells.filter{it.value > 0}.map { i -> RectF(i.position.x * cellSize, top, (i.position.x + 1) * cellSize, bottom) }
    }
}
