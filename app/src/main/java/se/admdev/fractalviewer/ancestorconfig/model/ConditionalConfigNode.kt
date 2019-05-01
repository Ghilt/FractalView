package se.admdev.fractalviewer.ancestorconfig.model

import se.admdev.fractalviewer.canvas.model.Cell
import se.admdev.fractalviewer.canvas.model.Coord

class ConditionalConfigNode(
    label: Char,
    val operandCondition: Operand,
    val operandTrue: Operand,
    val operandFalse: Operand
) : ConfigNode(label) {

    override fun compile(nodes: List<ConfigNode>): ((Coord, List<Cell>) -> Int) {
        // TODO Possibly need to optimize here
        if (operandCondition.label == null) {
            throw Exception("Error: OperandCondition.label is null in ConditionalConfigNode")
        }

        val condition = getNodeWithLabel(nodes, operandCondition.label).compile(nodes)

        var truePathRef: ((Coord, List<Cell>) -> Int)? = null
        var falsePathRef: ((Coord, List<Cell>) -> Int)? = null
        var truePathVal: Int? = null
        var falsePathVal: Int? = null

        if (operandTrue.label != null) {
            truePathRef = getNodeWithLabel(nodes, operandTrue.label).compile(nodes)
        } else {
            truePathVal = operandTrue.name.toInt()
        }

        if (operandFalse.label != null) {
            falsePathRef = getNodeWithLabel(nodes, operandFalse.label).compile(nodes)
        } else {
            falsePathVal = operandFalse.name.toInt()
        }

        return if (truePathRef != null && falsePathRef != null) {
            functionBothPathReference(condition, truePathRef, falsePathRef)
        } else if (truePathVal != null && falsePathRef != null) {
            functionFalsePathReference(condition, truePathVal, falsePathRef)
        } else if (truePathRef != null && falsePathVal != null) {
            functionTruePathReference(condition, truePathRef, falsePathVal)
        } else if (truePathVal != null && falsePathVal != null) {
            functionNoReference(condition, truePathVal, falsePathVal)
        } else {
            throw Exception("Error - ConditionalConfigNode.compile failed: $operandCondition, $operandFalse, $operandTrue")
        }
    }

    private fun functionBothPathReference(
        condition: ((Coord, List<Cell>) -> Int),
        truePath: ((Coord, List<Cell>) -> Int),
        falsePath: ((Coord, List<Cell>) -> Int)
    ): ((Coord, List<Cell>) -> Int) {
        return { c, cells ->
            if (condition.invoke(c, cells) != 0) {
                truePath.invoke(c, cells)
            } else {
                falsePath.invoke(c, cells)
            }
        }
    }

    private fun functionFalsePathReference(
        condition: ((Coord, List<Cell>) -> Int),
        trueValue: Int,
        falsePath: ((Coord, List<Cell>) -> Int)
    ): ((Coord, List<Cell>) -> Int) {
        return { c, cells ->
            if (condition.invoke(c, cells) != 0) {
                trueValue
            } else {
                falsePath.invoke(c, cells)
            }
        }
    }

    private fun functionTruePathReference(
        condition: ((Coord, List<Cell>) -> Int),
        truePath: ((Coord, List<Cell>) -> Int),
        falseVal: Int
    ): ((Coord, List<Cell>) -> Int) {
        return { c, cells ->
            if (condition.invoke(c, cells) != 0) {
                truePath.invoke(c, cells)
            } else {
                falseVal
            }
        }
    }

    private fun functionNoReference(
        condition: ((Coord, List<Cell>) -> Int),
        trueValue: Int,
        falseVal: Int
    ): ((Coord, List<Cell>) -> Int) {
        return { c, cells ->
            if (condition.invoke(c, cells) != 0) {
                trueValue
            } else {
                falseVal
            }
        }
    }

    companion object {
        /** Deserialization currently requires one unique field to identify correct subclass
         *  Cannot be obfuscated
         */
        const val ID_FIELD = "operandCondition"
    }
}