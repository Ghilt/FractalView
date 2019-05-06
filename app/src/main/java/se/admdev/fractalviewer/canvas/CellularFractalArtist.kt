package se.admdev.fractalviewer.canvas

import android.graphics.Path
import android.graphics.RectF
import se.admdev.fractalviewer.canvas.model.Cell

class CellularFractalArtist {


    //Adding to canvas as rectangles - Pretty laggy
    fun getIterationAsRectangles(iteration: Int, cells: List<Cell>): List<RectF> {
        val top = CELL_SIZE * iteration
        val bottom = CELL_SIZE * (iteration + 1)
        return cells.filter { it.value > 0 }
            .map { i -> RectF(i.position.x * CELL_SIZE, top, (i.position.x + 1) * CELL_SIZE, bottom) }
    }

    //Adding to canvas as one big path(per color) - Pretty laggy (a bit less, maybe)
    fun getIterationAsPathUpdate(iteration: Int, cells: List<Cell>): (List<Path>) -> Unit {
        val top = CELL_SIZE * iteration
        val bottom = CELL_SIZE * (iteration + 1)
        val cellsSplit = (1..5).map { i -> cells.filter { it.value == i } }
        return { paths ->
            paths.forEachIndexed { i, path ->
                cellsSplit[i].forEach { c ->
                    addCellToPath(c, path, top, bottom)
                }
            }
        }
    }

    //Adding to canvas as one path(per color) per iteration - Not as laggy!
    fun getIterationAsPaths(iteration: Int, cells: List<Cell>): List<Path> {
        val top = CELL_SIZE * iteration
        val bottom = CELL_SIZE * (iteration + 1)
        return (1..5)
            .map { i -> cells.filter { it.value == i } }
            .map { filteredCells -> filteredCells.fold(Path()) { acc, c -> acc.apply { addCellToPath(c, acc, top, bottom) } } }
    }

    private fun addCellToPath(c: Cell, p: Path, top: Float, bottom: Float) {
        p.addRect(c.position.x * CELL_SIZE, top, (c.position.x + 1) * CELL_SIZE, bottom, Path.Direction.CCW)
    }

    companion object {
        const val CELL_SIZE = 10f
    }
}

