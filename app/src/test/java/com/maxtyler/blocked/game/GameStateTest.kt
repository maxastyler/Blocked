package com.maxtyler.blocked.game

import junit.framework.TestCase

class GameStateTest : TestCase() {

    val defaultGame = GameState(10, 20).tryPosition(Vec2(5, 5))!!

    fun testTryPosition() {
        assertNull(defaultGame.tryPosition(Vec2(-1, -1)))
        assert(defaultGame.tryPosition(Vec2(5, 5)) is GameState)
    }

    fun testTryRotation() {
        assertEquals(defaultGame.tryRotation(Rotation.Left)?.pieceState?.rotation, Rotation.Left)
        assertEquals(
            defaultGame.tryRotation(Rotation.Right)
                ?.tryRotation(Rotation.Half)?.pieceState?.rotation, Rotation.Half
        )
    }

    fun testGetDroppedPosition() {
        assertEquals(defaultGame.getDroppedPosition(), Vec2(5, 1))
        assertEquals(defaultGame.tryRotation(Rotation.Right)?.getDroppedPosition(), Vec2(5, 3))
    }

    fun testGetShadow() {
        assertEquals(
            defaultGame.getShadow(),
            listOf(5 to 0, 6 to 0, 7 to 0, 8 to 0).map { (a, b) -> Vec2(a, b) })
    }

    fun testDrop() {
        val dropped = defaultGame.drop()
        assert(dropped is GameState.Dropped)
        assert(defaultGame.tryPosition(Vec2(4, 1))?.drop() is GameState.AddPieceToBoardReturn)
    }

    fun testHardDrop() {
        assert(defaultGame.hardDrop() is GameState.AddPieceToBoardReturn)
    }

    fun testEnsureEnoughPieces() {
        assert(defaultGame.copy(pieces = listOf()).ensureEnoughPieces().pieces.size > 6)
    }

    fun testAddScore() {
        val (newState, didLevelUp) = defaultGame.addScore(setOf(2, 3, 4, 5))
        assert(didLevelUp)
        assert(newState.score.lastClearWasTetris)
        assert(newState.score.level > 1)
        assert(newState.score.score > 0)
    }

    fun testAddPieceToBoard() {
        val ret = defaultGame.addPieceToBoard()
        assertFalse(ret.gameOver)
        assertFalse(ret.levelledUp)
        assertEquals(ret.gameState.pieceState.piece, defaultGame.pieces.first())
        val newRet = defaultGame.tryPosition(Vec2(5, defaultGame.settings.gameOverHeight))!!.addPieceToBoard()
        assert(newRet.gameOver)
        assertEquals(newRet.gameState.mode, GameState.Mode.GameOver)
        assertFalse(newRet.levelledUp)
    }

    fun testGetNextPiece() {
        val nextPieceState = defaultGame.getNextPiece()
        assertEquals(nextPieceState.pieceState.piece, defaultGame.pieces.first())
        assertEquals(nextPieceState.pieceState.position.y, nextPieceState.settings.startingHeight)
    }

    fun testHoldPiece() {}

    fun testResetPosition() {}

    fun testResetLockDelay() {}

    fun testUseLockRotation() {}

    fun testUseLockMovement() {}

    fun testPause() {}

    fun testResume() {}
}