package com.example.blocked.game

import junit.framework.TestCase

class Vec2Test : TestCase() {

    fun testTimes() {
        assertEquals(Vec2(-2, 3) * 3, Vec2(-6, 9))
    }

    fun testPlus() {
        assertEquals(Vec2(0, -3) + Vec2(2, 10), Vec2(2, 7))
    }

    fun testMinus() {
        assertEquals(Vec2(3, -2) - Vec2(4, 10), Vec2(-1, -12))
    }

    fun testRotate() {
        assertEquals(Vec2(2, 3).rotate(Rotation.None), Vec2(2, 3))
        assertEquals(Vec2(2, 3).rotate(Rotation.Right), Vec2(3, -2))
        assertEquals(Vec2(2, 3).rotate(Rotation.Right + Rotation.Right), Vec2(-2, -3))
        assertEquals(Vec2(2, 3).rotate(Rotation.Left), Vec2(-3, 2))
    }
}

