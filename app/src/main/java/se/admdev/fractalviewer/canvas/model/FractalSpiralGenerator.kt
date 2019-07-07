package se.admdev.fractalviewer.canvas.model

import se.admdev.fractalviewer.ancestorconfig.model.AncestorCore
import se.admdev.fractalviewer.canvas.model.Direction.*
import kotlin.math.abs

/**
 * This class uses a grid instead of the more linkedList'y approach used for the pyramid fractal.
 * The class conforms to the 'interfaces' dictated by other implementation though to save work.
 */
class FractalSpiralGenerator(private val core: AncestorCore) : FractalGenerator {

    private val frac = Array(1000) { IntArray(1000) } // TODO autoincrease size
    private val normalizedTargetCoord = Coord(1, 3)

    private val mid = frac.size / 2
    override var iterationsCompleted = 0
    private lateinit var lastIterationCoord: Coord
    private var lastIteration = mutableListOf<Cell>()

    private val originCell
        get() = Cell(frac[mid][mid], Coord(mid, mid), listOf())

    override fun generateNextIteration(): Boolean {
        lastIteration = mutableListOf()
        if (iterationsCompleted == 0) {
            frac[mid][mid] = 1
            iterationsCompleted++
            lastIterationCoord = Coord(mid, mid)
            lastIteration.add(originCell)
            return true
        }

        val radialDirection = getDirectionOfIteration(iterationsCompleted)
        val clockwiseDirection = radialDirection.getNextDirectionClockwise()
        val sizeOfIteration: Int = getSizeOfNextIteration(iterationsCompleted)
        val directionToExtendIn = if (iterationsCompleted == 1) radialDirection else clockwiseDirection
        val firstCoordOfIteration = directionToExtendIn.getNextCoord(lastIterationCoord)
        calculateAndInsertValue(
            firstCoordOfIteration,
            getAncestorsOfCellInIteration(firstCoordOfIteration, radialDirection)
        )
        var next = firstCoordOfIteration
        repeat(sizeOfIteration - 1) {
            next = clockwiseDirection.getNextCoord(next)
            calculateAndInsertValue(next, getAncestorsOfCellInIteration(next, radialDirection))
        }
        lastIterationCoord = next

//        Log.d("spx", "Iteration $radialDirection, $sizeOfIteration, $firstCoordOfIteration")

        iterationsCompleted++
        return true
    }

    private fun getSizeOfNextIteration(iterationsCompleted: Int): Int {
        return when (iterationsCompleted) {
            0 -> 1
            1 -> 2
            else -> 1 + iterationsCompleted / 2
        }
    }

    private fun getAncestorsOfCellInIteration(coord: Coord, direction: Direction): List<Cell> {
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

    private fun calculateAndInsertValue(coord: Coord, normalizedAncestors: List<Cell>) {
        frac[coord.x][coord.y] = core.calculateValue(normalizedTargetCoord, normalizedAncestors)
        lastIteration.add(Cell(frac[coord.x][coord.y], coord))
//        Log.d("spx", "${coord.x}, ${coord.y} = ${frac[coord.x][coord.y]}")
    }

    override fun getLastIteration(): List<Cell> = lastIteration

    private fun getDirectionOfIteration(iteration: Int): Direction {
        val clockwise = values()
        return clockwise[iteration % clockwise.size]
    }
}

enum class Direction {
    UP, RIGHT, DOWN, LEFT;

    fun getNextCoord(c: Coord) = when (this) {
        UP -> Coord(c.x, c.y - 1)
        RIGHT -> Coord(c.x + 1, c.y)
        DOWN -> Coord(c.x, c.y + 1)
        LEFT -> Coord(c.x - 1, c.y)
    }

    fun getNextDirectionClockwise() = when (this) {
        UP -> RIGHT
        RIGHT -> DOWN
        DOWN -> LEFT
        LEFT -> UP
    }
}