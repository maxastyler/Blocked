package com.example.blocked.game

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

data class Vec2(val x: Int, val y: Int) {
    operator fun plus(other: Vec2): Vec2 = Vec2(this.x + other.x, this.y + other.y)
    operator fun minus(other: Vec2): Vec2 = Vec2(this.x - other.x, this.y - other.y)
    operator fun times(other: Int): Vec2 = Vec2(this.x * other, this.y * other)

    fun rotate(rotation: Rotation): Vec2 = when (rotation) {
        Rotation.None -> this
        Rotation.Right -> Vec2(this.y, -this.x)
        Rotation.Half -> Vec2(-this.x, -this.y)
        Rotation.Left -> Vec2(-this.y, this.x)
    }
}