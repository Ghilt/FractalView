package se.admdev.fractalviewer.ancestorconfig.model

class ConditionalConfigNode(
    label: Char,
    val operandCondition: Operand,
    val operandTrue: Operand,
    val operandFalse: Operand
) : ConfigNode(label)