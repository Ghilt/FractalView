package se.admdev.fractalviewer.model

import android.graphics.Point
import java.util.ArrayList

class DragonCurve {

    private var segments = ArrayList<Point>()
    private var absolutePosition = ArrayList<Point>()
    private var turns = ArrayList<Int>()

    init {
        segments.add(DIRECTION_EAST)
        absolutePosition.add(DIRECTION_EAST)
        turns.add(LEFT)
        val initialTurns = intArrayOf(LEFT, LEFT, RIGHT)
        for (initialTurn in initialTurns) {
            val nextDirection = calculateNextDirection(segments[segments.size - 1], initialTurn)
            segments.add(nextDirection)
            turns.add(initialTurn)
            absolutePosition.add(calculateNextAbsolutePosition(absolutePosition[absolutePosition.size - 1], nextDirection))
        }
    }

    private fun calculateNextAbsolutePosition(startPos: Point, direction: Point): Point {
        return Point(startPos.x + direction.x, startPos.y + direction.y)
    }

    private fun calculateNextPart() {
        val currentSize = segments.size
        val positionOfInvert = currentSize / 2
        for (i in 0 until currentSize) {
            var turn = turns[i]
            if (i == positionOfInvert) {
                turn = 0
            }
            val direction = calculateNextDirection(segments[segments.size - 1], turn)
            turns.add(turn)
            segments.add(direction)
            absolutePosition.add(calculateNextAbsolutePosition(absolutePosition[absolutePosition.size - 1], direction))
        }
    }

    private fun calculateNextDirection(direction: Point, turn: Int): Point {
        var remap = if (turn == 0) 1 else -1
        remap = if (direction.x != 0) -remap else remap
        return Point(direction.y * remap, direction.x * remap)// turn 90degrees in correct direction
    }

    fun getSegmentAt(position: Int): Int {
        while (segments.size <= position) {
            calculateNextPart()
        }
        return turns[position]
    }

    fun getAbsolutePositionAt(position: Int): Point {
        while (segments.size <= position) {
            calculateNextPart()
        }
        return absolutePosition[position]
    }

    fun getDirectionAt(position: Int): Point {
        while (segments.size <= position) {
            calculateNextPart()
        }
        return segments[position]
    }

    fun getTurnAt(position: Int): Int {
        while (segments.size <= position) {
            calculateNextPart()
        }
        return turns[position]
    }

    companion object {
        val DIRECTION_NORTH = Point(0, 1)
        val DIRECTION_EAST = Point(1, 0)
        val DIRECTION_SOUTH = Point(0, -1)
        val DIRECTION_WEST = Point(-1, 0)
        val RIGHT = 0
        val LEFT = 1
    }
}

//https://medium.com/androiddevelopers/playing-with-paths-3fbc679a6f77