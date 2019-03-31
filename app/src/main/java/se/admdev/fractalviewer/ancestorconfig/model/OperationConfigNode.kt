package se.admdev.fractalviewer.ancestorconfig.model

import se.admdev.fractalviewer.canvas.model.Cell
import se.admdev.fractalviewer.canvas.model.Coord

class OperationConfigNode(
    label: Char,
    val groupOperator: GroupOperator,
    val tileSnapshot: List<List<AncestorTile>>,
    val operator: Operator?,
    val operand: Operand?
) : ConfigNode(label) {

    override fun gridSize(): Int {
        return tileSnapshot.size
    }

    override fun compile(nodes: List<ConfigNode>): ((Coord, List<Cell>) -> Int) {
        val targets = tileSnapshot.flatten().filter { it.selected }
        // TODO Possibly needs to optimize here
        // Current data structures should be thrown out and redone asap ^^'
        return { c, cells ->
            val lowestColumn = c.x - (gridSize() / 2)
            val lowestIteration = c.y - gridSize()
            val targetCells = cells.filter { cell -> targets.any { t -> cell.position == lowestColumn + t.x && cell.iteration == lowestIteration + t.y } }
            val gridCalculation = groupOperator.function.invoke(targetCells.map { it.value })
            if (operator == null) {
                gridCalculation
            } else {
                if (operand?.label == null) {
                    operator.function.invoke(gridCalculation, operand?.name?.toInt() ?: 0)

                } else {
                    val preReq = getNodeWithLabel(nodes, operand.label).compile(nodes)
                    operator.function.invoke(gridCalculation, preReq.invoke(c, cells))
                }
            }
        }
    }
}

