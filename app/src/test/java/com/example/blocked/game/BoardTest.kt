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

    fun testGetLineOffsets() {
        class b : Block

        val board = Board(width = 10, height = 10, blocks = mapOf())
        assertEquals(board.getLineOffsets(), Pair(setOf<Int>(), mapOf<Int, Int>()))
        val newBoard = board.copy(blocks = (0 until 10).map { Vec2(it, 0) to b() }
            .toMap() + (0 until 10).map { Vec2(it, 3) to b() }.toMap())
        assertEquals(
            newBoard.getLineOffsets(),
            Pair(
                setOf<Int>(0, 3),
                (1 until 3).map { it to it - 1 }.toMap() + (4 until 10).map { it to it - 2 }.toMap()
            )
        )
    }
}