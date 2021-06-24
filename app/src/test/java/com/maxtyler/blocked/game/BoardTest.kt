package com.maxtyler.blocked.game

import junit.framework.TestCase

class BoardTest : TestCase() {

    fun testAddPiece() {
        val board = Board(width = 10, height = 10, blocks = mapOf(Vec2(1, 1) to Piece.J))
        assertTrue(
            board.addPiece(
                PieceState(
                    Piece.I,
                    Vec2(1, 1),
                    Rotation.Half
                )
            ).blocks.keys == setOf(Vec2(1, 1), Vec2(1, 2), Vec2(1, 3))
        )
    }

    fun testIsValidPosition() {
        val board = Board(width = 10, height = 10, blocks = mapOf(Vec2(7, 7) to Piece.I))
        val ps = PieceState(Piece.I, Vec2(-1, -1), Rotation.None)
        assertFalse(
            board.isValidPosition(ps)
        )
        assertTrue(board.isValidPosition(ps.copy(position = Vec2(3, 3))))
        assertFalse(board.isValidPosition(PieceState(Piece.O, Vec2(7, 7), Rotation.None)))
    }

    fun testGetLineOffsets() {
        val board = Board(width = 10, height = 10, blocks = mapOf())
        assertEquals(board.getLineOffsets(), Pair(setOf<Int>(), mapOf<Int, Int>()))
        val newBoard = board.copy(blocks = (0 until 10).map { Vec2(it, 0) to Piece.I }
            .toMap() + (0 until 10).map { Vec2(it, 3) to Piece.J }.toMap())
        assertEquals(
            newBoard.getLineOffsets(),
            Pair(
                setOf<Int>(0, 3),
                (1 until 3).map { it to it - 1 }.toMap() + (4 until 10).map { it to it - 2 }.toMap()
            )
        )
    }
}