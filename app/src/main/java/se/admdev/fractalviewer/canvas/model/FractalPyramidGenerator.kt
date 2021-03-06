package se.admdev.fractalviewer.canvas.model

import android.graphics.Path
import se.admdev.fractalviewer.ancestorconfig.model.AncestorCore
import kotlin.math.absoluteValue

class FractalPyramidGenerator(private val core: AncestorCore) : FractalGenerator {
    private val frac = mutableMapOf<Int, List<Cell>>()

    override val iterationsCompleted
        get() = frac.size

    override fun generateNextIteration(): Boolean {
        if (iterationsCompleted == 0) {
            //todo, get starting seed from core for first iteration?
            frac[0] = listOf(Cell(1, Coord(0, 0), listOf()))
            return true
        }

        val lastItr = frac[iterationsCompleted - 1] ?: return false
        val smallestWindow = core.midX + 1
        val largestWindow = core.width - 1
        var startNeighbours = mutableListOf<List<Cell>>()
        val midNeighbours = lastItr.windowed(core.width)
        var endNeighbours = mutableListOf<List<Cell>>()

        if (largestWindow - smallestWindow + 1 >= lastItr.size) {

            // Special case encountered in the beginning of the fractal when the iteration is smaller than the input to the the next iteration
//            Log.d("spx", "Special special ${largestWindow - smallestWindow + 1} >= ${lastItr.size}")
            startNeighbours = MutableList(iterationsCompleted) { lastItr }
            endNeighbours = MutableList(iterationsCompleted) { lastItr }
        } else {

            for (i in smallestWindow..largestWindow) {
                startNeighbours.add(lastItr.take(i))
            }

            for (i in largestWindow downTo smallestWindow) {
                endNeighbours.add(lastItr.takeLast(i))
            }
        }

        if (midNeighbours.isEmpty() && endNeighbours.isNotEmpty()) {
            //Skip first window if no midNeighbours. Prevent representing it here as well as in startNeighbours
            endNeighbours.removeAt(0)
        }

        val endIndexCalculation = { windowCount: Int, windowSize: Int, index: Int ->
            // Sorry reason, sorry logic, this really got out of hand, unnecessarily complex
            // Could possibly be cool to extract 'ancestor finding' logic entirely to core, and get rid of the rigid square-ancestor-area
            windowSize - (windowCount - index)
        }
//        Log.d("spx", "neigh s.${startNeighbours.size} m.${midNeighbours.size} e.${endNeighbours.size}")

        val leftEdgeCell = calculateLeftEdgeCell(lastItr)
        val newItrStart = calculateChunkFromNeighbours(startNeighbours) { _, _, index -> index }
        val newItrMid = calculateChunkFromNeighbours(midNeighbours) { _, _, _ -> core.midX }
        val newItEnd = calculateChunkFromNeighbours(endNeighbours, endIndexCalculation)
        val rightEdgeCell = calculateRightEdgeCell(lastItr)

        frac[iterationsCompleted] = listOf(leftEdgeCell) + newItrStart + newItrMid + newItEnd + listOf(rightEdgeCell)

//        Log.d("spx", "Size: ${frac.size}")

        //Quick and dirty temp debug logging
//        val logPyramid: String =
//            frac.flatMap { (_, value) -> "${value.fold("") { acc, cell -> "$acc${if (cell.value == 0) "_" else "${cell.value}"}" }} \n".asIterable() }
//                .fold("") { acc, charList -> acc + charList }
//
//        val initialPadding = logPyramid.lines().takeLast(2).first().length / 2
//        val lp = logPyramid
//            .lines()
//            .mapIndexed { i, s -> " ".repeat(max(0, initialPadding - i)) + s }
//            .fold("") { acc, charList -> "$acc\n$charList" }
//
//        Log.d("spx", "pyramid \n$lp")

        return true
    }

    private fun calculateChunkFromNeighbours(
        neighbours: List<List<Cell>>,
        nearestNeighbourIndex: (windowCount: Int, windowSize: Int, index: Int) -> Int
    ): List<Cell> {
        return neighbours.mapIndexed { i, w ->
            val nearest = w[nearestNeighbourIndex(neighbours.size, w.size, i)]
            val ancestors = translateAncestors(w, nearest)

            val coord = Coord(nearest.position.x, iterationsCompleted)

            Cell(core.calculateValue(coord, ancestors), coord, ancestors)
        }
    }

    private fun calculateLeftEdgeCell(lastItr: List<Cell>): Cell {
        val oldEdge = lastItr.first()
        val ancestors = oldEdge.ancestors
            .filter { oldEdge.position.y - it.position.y < core.height }
            .filter { (oldEdge.position.x - it.position.x).absoluteValue < core.midX }
            .plus(lastItr.take(core.midX))

        val coord = Coord(-iterationsCompleted, iterationsCompleted)

        return Cell(core.calculateValue(coord, ancestors), coord, ancestors)
    }

    private fun calculateRightEdgeCell(lastItr: List<Cell>): Cell {
        val oldEdge = lastItr.last()
        val ancestors = oldEdge.ancestors
            .filter { oldEdge.position.y - it.position.y < core.height }
            .filter { oldEdge.position.x - it.position.x < core.midX }
            .plus(lastItr.takeLast(core.midX))

        val coord = Coord(iterationsCompleted, iterationsCompleted)

        return Cell(core.calculateValue(coord, ancestors), coord, ancestors)
    }

    private fun translateAncestors(neighbours: List<Cell>, nearest: Cell): List<Cell> {
        /* Example
                ┌─┬─┬─┐  <--- filter out first row of precious cells ancestors
                ┌─┬─┬─┐
                ├─┼─┼─┤  <--- Ancestors in common with previous cell
                ├─┼─┼─┤
                └─┴─┴─┘
                └─┴─┴─┘  <--- Add new row of precious cells neighbours
                   x     <--- Current cell
            // Todo possible need to do something more clever if this is to slow
         */
        return nearest.ancestors
            .filter { nearest.position.y - it.position.y < core.height }
            .plus(neighbours)
    }

    override fun getLastIteration(): List<Cell> {
        return frac[iterationsCompleted - 1] ?: listOf()
    }

    /* To be used for miniature thumbnail of fractal */
    /* TODO clean up code here, is a bit messy responsibility wise */
    fun iterateOver(action: (List<Cell>) -> Path): List<Path> =
        frac.map { (_, value) -> action(value) }

}