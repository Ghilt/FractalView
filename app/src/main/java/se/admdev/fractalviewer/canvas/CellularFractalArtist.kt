package se.admdev.fractalviewer.canvas

import android.graphics.Path
import android.graphics.RectF
import se.admdev.fractalviewer.canvas.model.Cell

class CellularFractalArtist {

    private val cellSize = 10f

    fun getIterationAsRectangles(iteration: Int, cells: List<Cell>): List<RectF> {
        val top = cellSize * iteration
        val bottom = cellSize * (iteration + 1)
        return cells.filter { it.value > 0 }
            .map { i -> RectF(i.position.x * cellSize, top, (i.position.x + 1) * cellSize, bottom) }
    }

    fun getIterationAsPathUpdate(iteration: Int, cells: List<Cell>): (List<Path>) -> Unit {
        val top = cellSize * iteration
        val bottom = cellSize * (iteration + 1)
        val cellsSplit = (1..5).map { i -> cells.filter { it.value == i } }
        return { paths ->
            paths.forEachIndexed { i, path ->
                cellsSplit[i].forEach { c ->
                    path.addRect(c.position.x * cellSize, top, (c.position.x + 1) * cellSize, bottom, Path.Direction.CCW)
                }
            }
        }
    }
}
