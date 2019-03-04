package se.admdev.fractalviewer.canvas.model

data class Cell(val value: Int, val iteration: Int, val position: Int, val ancestors: List<Cell>)