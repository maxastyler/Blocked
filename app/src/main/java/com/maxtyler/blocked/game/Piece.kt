package com.maxtyler.blocked.game

import androidx.compose.ui.graphics.Color

/**
 * An enum class containing the different possible pieces
 */
enum class Piece {
    I, J, L, O, S, T, Z;

    /**
     * Convert each piece to its letter
     */
    override fun toString(): String = when (this) {
        I -> "I"
        J -> "J"
        L -> "L"
        O -> "O"
        S -> "S"
        T -> "T"
        Z -> "Z"
    }

    /**
     * @param rotation The rotation of the piece
     * @return A list of the positions of the blocks in the piece
     */
    fun getCoordinates(rotation: Rotation): List<Vec2> =
        when (this) {
            I -> when (rotation) {
                Rotation.None -> listOf(Vec2(0, -1), Vec2(1, -1), Vec2(2, -1), Vec2(3, -1))
                Rotation.Right -> listOf(Vec2(2, 0), Vec2(2, -1), Vec2(2, -2), Vec2(2, -3))
                Rotation.Half -> listOf(Vec2(0, -2), Vec2(1, -2), Vec2(2, -2), Vec2(3, -2))
                Rotation.Left -> listOf(Vec2(1, 0), Vec2(1, -1), Vec2(1, -2), Vec2(1, -3))
            }
            J -> when (rotation) {
                Rotation.None -> listOf(Vec2(0, 0), Vec2(0, -1), Vec2(1, -1), Vec2(2, -1))
                Rotation.Right -> listOf(Vec2(1, 0), Vec2(2, 0), Vec2(1, -1), Vec2(1, -2))
                Rotation.Half -> listOf(Vec2(0, -1), Vec2(1, -1), Vec2(2, -1), Vec2(2, -2))
                Rotation.Left -> listOf(Vec2(1, 0), Vec2(1, -1), Vec2(0, -2), Vec2(1, -2))
            }
            L -> when (rotation) {
                Rotation.None -> listOf(Vec2(2, 0), Vec2(0, -1), Vec2(1, -1), Vec2(2, -1))
                Rotation.Right -> listOf(Vec2(1, 0), Vec2(1, -1), Vec2(1, -2), Vec2(2, -2))
                Rotation.Half -> listOf(Vec2(0, -1), Vec2(1, -1), Vec2(2, -1), Vec2(0, -2))
                Rotation.Left -> listOf(Vec2(0, 0), Vec2(1, 0), Vec2(1, -1), Vec2(1, -2))
            }
            O -> when (rotation) {
                Rotation.None -> listOf(Vec2(0, 0), Vec2(1, 0), Vec2(0, -1), Vec2(1, -1))
                Rotation.Right -> listOf(Vec2(0, 0), Vec2(1, 0), Vec2(0, -1), Vec2(1, -1))
                Rotation.Half -> listOf(Vec2(0, 0), Vec2(1, 0), Vec2(0, -1), Vec2(1, -1))
                Rotation.Left -> listOf(Vec2(0, 0), Vec2(1, 0), Vec2(0, -1), Vec2(1, -1))
            }
            S -> when (rotation) {
                Rotation.None -> listOf(Vec2(1, 0), Vec2(2, 0), Vec2(0, -1), Vec2(1, -1))
                Rotation.Right -> listOf(Vec2(1, 0), Vec2(1, -1), Vec2(2, -1), Vec2(2, -2))
                Rotation.Half -> listOf(Vec2(1, -1), Vec2(2, -1), Vec2(0, -2), Vec2(1, -2))
                Rotation.Left -> listOf(Vec2(0, 0), Vec2(0, -1), Vec2(1, -1), Vec2(1, -2))
            }
            T -> when (rotation) {
                Rotation.None -> listOf(Vec2(1, 0), Vec2(0, -1), Vec2(1, -1), Vec2(2, -1))
                Rotation.Right -> listOf(Vec2(1, 0), Vec2(1, -1), Vec2(2, -1), Vec2(1, -2))
                Rotation.Half -> listOf(Vec2(0, -1), Vec2(1, -1), Vec2(2, -1), Vec2(1, -2))
                Rotation.Left -> listOf(Vec2(1, 0), Vec2(0, -1), Vec2(1, -1), Vec2(1, -2))
            }
            Z -> when (rotation) {
                Rotation.None -> listOf(Vec2(0, 0), Vec2(1, 0), Vec2(1, -1), Vec2(2, -1))
                Rotation.Right -> listOf(Vec2(2, 0), Vec2(1, -1), Vec2(2, -1), Vec2(1, -2))
                Rotation.Half -> listOf(Vec2(0, -1), Vec2(1, -1), Vec2(1, -2), Vec2(2, -2))
                Rotation.Left -> listOf(Vec2(1, 0), Vec2(0, -1), Vec2(1, -1), Vec2(0, -2))
            }
        }

    /**
     * Get a list of the kicks to try when rotating the piece
     * @param from the rotation we're coming from
     * @param to the rotation we're going to
     * @return The list of kicks
     */
    fun getKicks(from: Rotation, to: Rotation): List<Vec2> =
        when (this) {
            I -> when (from to to) {
                (Rotation.None to Rotation.Right) -> listOf(
                    Vec2(0, 0),
                    Vec2(-2, 0),
                    Vec2(1, 0),
                    Vec2(-2, -1),
                    Vec2(1, 2)
                )
                (Rotation.Right to Rotation.None) -> listOf(
                    Vec2(0, 0),
                    Vec2(2, 0),
                    Vec2(-1, 0),
                    Vec2(2, 1),
                    Vec2(-1, -2)
                )
                (Rotation.Right to Rotation.Half) -> listOf(
                    Vec2(0, 0),
                    Vec2(-1, 0),
                    Vec2(2, 0),
                    Vec2(-1, 2),
                    Vec2(2, -1)
                )
                (Rotation.Half to Rotation.Right) -> listOf(
                    Vec2(0, 0),
                    Vec2(1, 0),
                    Vec2(-2, 0),
                    Vec2(1, -2),
                    Vec2(-2, 1)
                )
                (Rotation.Half to Rotation.Left) -> listOf(
                    Vec2(0, 0),
                    Vec2(2, 0),
                    Vec2(-1, 0),
                    Vec2(2, 1),
                    Vec2(-1, -2)
                )
                (Rotation.Left to Rotation.Half) -> listOf(
                    Vec2(0, 0),
                    Vec2(-2, 0),
                    Vec2(1, 0),
                    Vec2(-2, -1),
                    Vec2(1, 2)
                )
                (Rotation.Left to Rotation.None) -> listOf(
                    Vec2(0, 0),
                    Vec2(1, 0),
                    Vec2(-2, 0),
                    Vec2(1, -2),
                    Vec2(-2, 1)
                )
                (Rotation.None to Rotation.Left) -> listOf(
                    Vec2(0, 0),
                    Vec2(-1, 0),
                    Vec2(2, 0),
                    Vec2(-1, 2),
                    Vec2(2, -1)
                )
                else -> throw IllegalStateException()
            }
            else -> when (from to to) {
                (Rotation.None to Rotation.Right) -> listOf(
                    Vec2(0, 0),
                    Vec2(-1, 0),
                    Vec2(-1, 1),
                    Vec2(0, -2),
                    Vec2(-1, -2)
                )
                (Rotation.Right to Rotation.None) -> listOf(
                    Vec2(0, 0),
                    Vec2(1, 0),
                    Vec2(1, -1),
                    Vec2(0, 2),
                    Vec2(1, 2)
                )
                (Rotation.Right to Rotation.Half) -> listOf(
                    Vec2(0, 0),
                    Vec2(1, 0),
                    Vec2(1, -1),
                    Vec2(0, 2),
                    Vec2(1, 2)
                )
                (Rotation.Half to Rotation.Right) -> listOf(
                    Vec2(0, 0),
                    Vec2(-1, 0),
                    Vec2(-1, 1),
                    Vec2(0, -2),
                    Vec2(-1, -2)
                )
                (Rotation.Half to Rotation.Left) -> listOf(
                    Vec2(0, 0),
                    Vec2(1, 0),
                    Vec2(1, 1),
                    Vec2(0, -2),
                    Vec2(1, -2)
                )
                (Rotation.Left to Rotation.Half) -> listOf(
                    Vec2(0, 0),
                    Vec2(-1, 0),
                    Vec2(-1, -1),
                    Vec2(0, 2),
                    Vec2(-1, 2)
                )
                (Rotation.Left to Rotation.None) -> listOf(
                    Vec2(0, 0),
                    Vec2(-1, 0),
                    Vec2(-1, -1),
                    Vec2(0, 2),
                    Vec2(-1, 2)
                )
                (Rotation.None to Rotation.Left) -> listOf(
                    Vec2(0, 0),
                    Vec2(1, 0),
                    Vec2(1, 1),
                    Vec2(0, -2),
                    Vec2(1, -2)
                )
                else -> throw IllegalStateException()
            }
        }

    /**
     * The offset that should be applied to the piece when placing it
     */
    val offset: Int
        get() = when (this) {
            I -> 2
            J -> 2
            L -> 2
            O -> 1
            S -> 2
            T -> 2
            Z -> 2
        }

    fun colour(colourSettings: ColourSettings): Color = when (this) {
        I -> colourSettings.IColour
        J -> colourSettings.JColour
        L -> colourSettings.LColour
        Z -> colourSettings.ZColour
        S -> colourSettings.SColour
        T -> colourSettings.TColour
        O -> colourSettings.OColour
    }

    companion object {
        /**
         * Get a list of all of the pieces in a random order
         * @return The list of shuffled pieces
         */
        fun shuffled(): List<Piece> = listOf(I, J, L, O, S, T, Z).shuffled()
    }
}
