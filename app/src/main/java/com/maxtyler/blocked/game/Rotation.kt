package com.maxtyler.blocked.game

enum class Rotation {
    Left, Right, None, Half;

    fun toInt() = when (this) {
        None -> 0
        Right -> 1
        Half -> 2
        Left -> 3
    }

    companion object {
        fun fromInt(i: Int): Rotation = when (Math.floorMod(i, 4)) {
            0 -> None
            1 -> Right
            2 -> Half
            3 -> Left
            else -> throw ArithmeticException()
        }
    }

    operator fun plus(other: Rotation): Rotation = fromInt(this.toInt() + other.toInt())
    operator fun minus(other: Rotation): Rotation = fromInt(this.toInt() - other.toInt())
}
