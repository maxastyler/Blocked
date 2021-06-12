package com.example.blocked.game

import junit.framework.TestCase

class RotationTest : TestCase() {

    fun testToInt() {
        assertEquals(Rotation.fromInt(Rotation.Left.toInt() + Rotation.Right.toInt()), Rotation.None)
    }

    fun testPlus() {
        assertEquals(Rotation.Left + Rotation.Right, Rotation.None)
        assertEquals(Rotation.Half + Rotation.Right + Rotation.Left, Rotation.Half)
        assertEquals(Rotation.None + Rotation.None, Rotation.None)
    }

    fun testMinus() {
        assertEquals(Rotation.Left - Rotation.Right, Rotation.Half)
    }
}