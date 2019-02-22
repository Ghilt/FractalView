package se.admdev.fractalviewer.model

class AncestorCore {
    fun calculateValue(ancestors: List<Cell>): Int {
        return ancestors.fold(0) { acc, cell -> (acc + cell.value) % 2 }
    }

    val width: Int = 7
    val height: Int = 7

    val midX = width / 2
}
