package com.example.blocked.game

import junit.framework.TestCase

class BoardTest : TestCase() {

    fun testIsValidPosition() {
        class b : Block

        val board = Board(width = 10, height = 10, blocks = mapOf(Vec2(7, 7) to b()))
        assertFalse(
            board.isValidPosition(
                Piece.I,
                position = Vec2(-1, -1),
                rotation = Rotation.None
            )
        )
        assertTrue(board.isValidPosition(Piece.I, position = Vec2(3, 3), rotation = Rotation.None))
        assertFalse(board.isValidPosition(Piece.O, position = Vec2(7, 7), rotation = Rotation.None))
    }
}