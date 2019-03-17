package se.admdev.fractalviewer.ancestorconfig.model

class OperationConfigNode (
    label: Char,
    val groupOperator: GroupOperator,
    val tileSnapshot: List<List<AncestorTile>>,
    val operator: Operator?,
    val operand: Operand?
) : ConfigNode(label)