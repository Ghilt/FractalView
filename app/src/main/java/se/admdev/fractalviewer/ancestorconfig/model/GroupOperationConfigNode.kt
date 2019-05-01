package se.admdev.fractalviewer.ancestorconfig.model

import se.admdev.fractalviewer.canvas.model.Cell
import se.admdev.fractalviewer.canvas.model.Coord

class GroupOperationConfigNode(
    label: Char,
    val groupOperator: GroupOperator,
    val tileSnapshot: List<List<AncestorTile>>,
    val operator: Operator?,
    val operand: Operand?
) : ConfigNode(label) {

    internal val gridSize = tileSnapshot.size
    private val targets = tileSnapshot.flatten().filter { it.selected }

    override fun compile(nodes: List<ConfigNode>): ((Coord, List<Cell>) -> Int) {
        // TODO Possibly need to optimize here
        // Current data structures should be thrown out and redone asap ^^'
        // also nodes are calculated multiple times if they're used in many nodes: once is enough obviously

        return if (operand?.label == null) {
            val value = operand?.name?.toInt() ?: 0
            functionValueOperand(value)
        } else {
            val operandRef = getNodeWithLabel(nodes, operand.label).compile(nodes)
            functionReferenceOperand(operandRef)
        }
    }

    private fun functionReferenceOperand(operand: ((Coord, List<Cell>) -> Int)): ((Coord, List<Cell>) -> Int) {
        return { c, cells ->
            val targetCells = cells.filter { inTargetList(c, it) }
            val gridCalculation = groupOperator.function.invoke(targetCells.map { it.value })
            operator?.function?.invoke(gridCalculation, operand.invoke(c, cells)) ?: gridCalculation
        }
    }

    private fun functionValueOperand(value: Int): ((Coord, List<Cell>) -> Int) {
        return { c, cells ->
            val targetCells = cells.filter { inTargetList(c, it) }
            val gridCalculation = groupOperator.function.invoke(targetCells.map { it.value })
            operator?.function?.invoke(gridCalculation, value) ?: gridCalculation
        }
    }

    private fun inTargetList(coord: Coord, c: Cell): Boolean {
        // Messy, fix 'some time'
        val lowestColumn = coord.x - (gridSize / 2)
        val lowestIteration = coord.y - gridSize
        return targets.any { t -> c.position.x == lowestColumn + t.x && c.position.y == lowestIteration + t.y }
    }

    companion object {
        /** Deserialization currently requires one unique field to identify correct subclass
         *  Cannot be obfuscated
         */
        const val ID_FIELD = "tileSnapshot"
    }
}

