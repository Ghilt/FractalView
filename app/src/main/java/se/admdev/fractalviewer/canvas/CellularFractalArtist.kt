package se.admdev.fractalviewer.canvas

import android.graphics.Path
import android.graphics.RectF
import se.admdev.fractalviewer.canvas.model.Cell

class CellularFractalArtist {

    //Adding to canvas as rectangles - Pretty laggy
    @Deprecated("Should not be used unless investigating optimization again")
    fun getIterationAsRectangles(iteration: Int, cells: List<Cell>): List<RectF> {
        val top = CELL_SIZE * iteration
        val bottom = CELL_SIZE * (iteration + 1)
        return cells.filter { it.value > 0 }
            .map { i -> RectF(i.position.x * CELL_SIZE, top, (i.position.x + 1) * CELL_SIZE, bottom) }
    }

    //Adding to canvas as one big path - Pretty laggy (a bit less, maybe)
    @Deprecated("Should not be used unless investigating optimization again")
    fun getIterationAsPathUpdate(iteration: Int, cells: List<Cell>): (Path) -> Unit {
        val top = CELL_SIZE * iteration
        val bottom = CELL_SIZE * (iteration + 1)
        val nonZero = cells.filter { it.value != 0 }
        return { path ->
            nonZero.forEach { c ->
                addCellToPath(c, path, top, bottom)
            }
        }
    }

    //Adding to canvas as one path per iteration - Not as laggy!
    fun getIterationAsPaths(iteration: Int, cells: List<Cell>): Path {
        val top = CELL_SIZE * iteration
        val bottom = CELL_SIZE * (iteration + 1)
        val nonZero = cells.filter { it.value != 0 }
        return nonZero.fold(Path()) { acc, c ->
            acc.apply {
                addCellToPath(c, acc, top, bottom)
            }
        }
    }

    private fun addCellToPath(c: Cell, p: Path, top: Float, bottom: Float) {
        p.addRect(c.position.x * CELL_SIZE, top, (c.position.x + 1) * CELL_SIZE, bottom, Path.Direction.CCW)
    }

    companion object {
        const val CELL_SIZE = 10f
    }
}

