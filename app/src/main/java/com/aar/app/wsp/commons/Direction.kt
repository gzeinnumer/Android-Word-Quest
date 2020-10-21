package com.aar.app.wsp.commons

import kotlin.math.abs

/**
 * Created by abdularis on 29/06/17.
 *
 * Direction (Arah)
 */
enum class Direction(
    @JvmField
    val xOffset: Int,
    @JvmField
    val yOffset: Int
) {
    NONE(0, 0),
    EAST(1, 0),
    WEST(-1, 0),
    NORTH(0, -1),
    SOUTH(0, 1),
    SOUTH_EAST(1, 1),
    NORTH_WEST(-1, -1),
    SOUTH_WEST(-1, 1),
    NORTH_EAST(1, -1);

    fun nextDirection(): Direction {
        var idx = (ordinal + 1) % values().size
        if (values()[idx] == NONE) idx++
        return values()[idx]
    }

    companion object {
        @JvmStatic
        fun fromLine(start: GridIndex, end: GridIndex): Direction {
            // Horizontal
            if (start.row == end.row && start.col != end.col) {
                return if (start.col < end.col) EAST else WEST
            }

            // Vertical
            if (start.col == end.col && start.row != end.row) {
                return if (start.row < end.row) SOUTH else NORTH
            }

            // Diagonal
            val diffX = abs(start.col - end.col)
            val diffY = abs(start.row - end.row)
            if (diffX == diffY && diffX != 0) {
                if (start.col < end.col && start.row < end.row) return SOUTH_EAST
                if (start.col > end.col && start.row > end.row) return NORTH_WEST
                return if (start.col > end.col && start.row < end.row) SOUTH_WEST else NORTH_EAST
            }

            return NONE
        }
    }
}