package com.example.blocked.game

enum class Piece {
    I, J, L, O, S, T, Z;

    override fun toString(): String = when (this) {
        I -> "I"
        J -> "J"
        L -> "L"
        O -> "O"
        S -> "S"
        T -> "T"
        Z -> "Z"
    }

    fun getCoordinates(rotation: Rotation) =
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

    fun getKicks(from: Rotation, to: Rotation) =
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

    companion object {
        fun shuffled(): List<Piece> = listOf(I, J, L, O, S, T, Z).shuffled()
    }
}
