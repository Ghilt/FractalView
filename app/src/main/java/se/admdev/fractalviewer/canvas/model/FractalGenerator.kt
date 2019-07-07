package se.admdev.fractalviewer.canvas.model

interface FractalGenerator {

    fun getLastIteration(): List<Cell>
    fun generateNextIteration(): Boolean

    val iterationsCompleted: Int
}