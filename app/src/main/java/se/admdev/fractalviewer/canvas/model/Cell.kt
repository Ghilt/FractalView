package se.admdev.fractalviewer.canvas.model

data class Cell(val value: Int, val position: Coord, val ancestors: List<Cell>)

data class Coord(val x: Int, val y: Int)
