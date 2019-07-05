package se.admdev.fractalviewer.ancestorconfig.model

import se.admdev.fractalviewer.canvas.model.Cell
import se.admdev.fractalviewer.canvas.model.Coord

class OperationConfigNode(
    label: Char,
    val firstOperand: Operand,
    val operator: Operator,
    val secondOperand: Operand
) : ConfigNode(label) {

    override fun compile(nodes: List<ConfigNode>): ((Coord, List<Cell>) -> Int) {
        // TODO Possibly need to optimize here
        // Current data structures should be thrown out and redone asap ^^'
        // also nodes are calculated multiple times if they're used in many nodes: once is enough obviously

        var firstOperandRef: ((Coord, List<Cell>) -> Int)? = null
        var secondOperandRef: ((Coord, List<Cell>) -> Int)? = null
        var firstOperandVal: Int? = null
        var secondOperandVal: Int? = null

        if (firstOperand.label != null) {
            firstOperandRef = getNodeWithLabel(nodes, firstOperand.label).compile(nodes)
        } else {
            firstOperandVal = firstOperand.name.toInt()
        }

        if (secondOperand.label != null) {
            secondOperandRef = getNodeWithLabel(nodes, secondOperand.label).compile(nodes)
        } else {
            secondOperandVal = secondOperand.name.toInt()
        }

        //TODO extract this structure an keep in common with condition node

        return if (firstOperandRef != null && secondOperandRef != null) {
            functionBothReference(firstOperandRef, secondOperandRef)
        } else if (firstOperandVal != null && secondOperandRef != null) {
            functionSecondReference(firstOperandVal, secondOperandRef)
        } else if (firstOperandRef != null && secondOperandVal != null) {
            functionFirstReference(firstOperandRef, secondOperandVal)
        } else if (firstOperandVal != null && secondOperandVal != null) {
            functionNoReference(firstOperandVal, secondOperandVal)
        } else {
            throw Exception("Error - ConditionalConfigNode.compile failed: $firstOperand, $operator, $secondOperand")
        }
    }

    private fun functionBothReference(
        opRef1: ((Coord, List<Cell>) -> Int),
        opRef2: ((Coord, List<Cell>) -> Int)
    ): ((Coord, List<Cell>) -> Int) {
        return { c, cells -> operator.function(opRef1.invoke(c, cells), opRef2.invoke(c, cells)) }
    }

    private fun functionSecondReference(
        opVal1: Int,
        opRef2: ((Coord, List<Cell>) -> Int)
    ): ((Coord, List<Cell>) -> Int) {
        return { c, cells -> operator.function(opVal1, opRef2.invoke(c, cells)) }
    }

    private fun functionFirstReference(
        opRef1: ((Coord, List<Cell>) -> Int),
        opVal2: Int
    ): ((Coord, List<Cell>) -> Int) {
        return { c, cells -> operator.function(opRef1.invoke(c, cells), opVal2) }
    }


    private fun functionNoReference(
        opVal1: Int,
        opVal2: Int
    ): ((Coord, List<Cell>) -> Int) {
        return { _, _ -> operator.function(opVal1, opVal2) }
    }
}

