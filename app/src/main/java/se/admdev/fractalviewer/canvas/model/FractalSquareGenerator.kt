package se.admdev.fractalviewer.canvas.model

import android.util.Log
import se.admdev.fractalviewer.ancestorconfig.model.AncestorCore
import se.admdev.fractalviewer.canvas.model.Direction.*
import kotlin.math.abs

/**
 * This class uses a grid instead of the more linkedList'y approach used for the pyramid fractal.
 * The class conforms to the 'interfaces' dictated by other implementation though to save work.
 */
class FractalSquareGenerator(private val core: AncestorCore) : FractalGenerator {

    private val frac = Array(DATA_STRUCTURE_SIZE) { IntArray(DATA_STRUCTURE_SIZE) } // TODO autoincrease size
    private val normalizedTargetCoord = Coord(core.width / 2, core.height)

    private val mid = frac.size / 2
    override var iterationsCompleted = 0
    private var lastIteration = mutableListOf<Cell>()

    override fun generateNextIteration(): Boolean {
        lastIteration = mutableListOf()
        if (iterationsCompleted == 0) {
            frac[mid][mid] = 1
            iterationsCompleted++
            lastIteration.add(Cell(frac[mid][mid], centerOnOrigin(Coord(mid, mid)), listOf()))
            return true
        } else if (iterationsCompleted >= DATA_STRUCTURE_SIZE - core.width) {
            // Size limit reached
            return false
        }

        val lowerLimit = mid - iterationsCompleted
        val upperLimit = mid + iterationsCompleted

        val topRow = (lowerLimit..upperLimit).map { Coord(it, lowerLimit) }
        val rightColumn = (lowerLimit + 1 until upperLimit).map { Coord(upperLimit, it) }
        val bottomRow = (lowerLimit..upperLimit).map { Coord(it, upperLimit) }
        val leftColumn = (lowerLimit + 1 until upperLimit).map { Coord(lowerLimit, it) }

        calculateVector(topRow, UP)
        calculateVector(rightColumn, RIGHT)
        calculateVector(bottomRow, DOWN)
        calculateVector(leftColumn, LEFT)

        //The iteration is finished, now commit to grid
        lastIteration.forEach { frac[mid + it.position.x][mid + it.position.y] = it.value }

        Log.d("spx", "Iteration $lowerLimit, $upperLimit, $iterationsCompleted")

        iterationsCompleted++
        return true
    }

    private fun calculateVector(vector: List<Coord>, dir: Direction) {
        vector.forEach { calculateValue(it, getAncestors(it, dir)) }
    }

    private fun getAncestors(coord: Coord, direction: Direction): List<Cell> {
        val upperLeftCornerX = when (direction) {
            UP, DOWN -> coord.x - core.width / 2
            RIGHT -> coord.x - core.width
            LEFT -> coord.x + 1
        }
        val upperLeftCornerY = when (direction) {
            UP -> coord.y + 1
            RIGHT, LEFT -> coord.y - core.height / 2
            DOWN -> coord.y - core.height
        }

        val xMax = core.width - 1
        val yMax = core.height - 1

        val list = mutableListOf<Cell>()
        for (x in 0 until core.width) {
            for (y in 0 until core.height) {
                // As the ancestor core(Or rather GroupOperationConfigNode) has no sense of direction
                // I fake it here for now by manipulating the values and normalizing the ancestor coordinates
                val normalizedAndRotated = when (direction) {
                    UP -> Coord(abs(x - xMax), abs(y - xMax))
                    RIGHT -> Coord(abs(y - yMax), x)
                    DOWN -> Coord(x, y)
                    LEFT -> Coord(y, abs(x - xMax))
                }

                // TODO do cache search here instead of creating so many cell objects ⇓ ⇑
                list.add(Cell(frac[upperLeftCornerX + x][upperLeftCornerY + y], normalizedAndRotated))
            }
        }
        return list
    }

    private fun calculateValue(coord: Coord, normalizedAncestors: List<Cell>) {
        val valueOfCell = core.calculateValue(normalizedTargetCoord, normalizedAncestors)
        lastIteration.add(Cell(valueOfCell, centerOnOrigin(coord)))
    }

    private fun centerOnOrigin(c: Coord) = Coord(c.x - mid, c.y - mid)

    override fun getLastIteration(): List<Cell> = lastIteration


    companion object {
        const val DATA_STRUCTURE_SIZE = 1500
    }
}
