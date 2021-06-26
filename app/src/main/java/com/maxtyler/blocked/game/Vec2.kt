package com.maxtyler.blocked.game


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

    override fun toString(): String = "$x,$y"

    companion object {
        fun fromString(s: String): Vec2 {
            val split = s.split(",")
            return Vec2(split[0].toInt(), split[1].toInt())
        }
    }
}