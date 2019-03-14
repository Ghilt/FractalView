package se.admdev.fractalviewer.ancestorconfig.model

class ConfigNode(
    val label: Char,
    val tileSnapshot: List<List<AncestorTile>>,
    val operator: Operator?,
    val operand: Operand?
)