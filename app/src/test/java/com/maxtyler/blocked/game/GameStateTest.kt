package com.maxtyler.blocked.game

import junit.framework.TestCase
import org.junit.Assert.assertThrows
import java.lang.IllegalStateException
import java.lang.reflect.Executable

class GameStateTest : TestCase() {

    val defaultGame = GameState(10, 20).tryPosition(Vec2(5, 5))!!

    fun testTryPosition() {
        assertNull(defaultGame.tryPosition(Vec2(-1, -1)))
        assert(defaultGame.tryPosition(Vec2(5, 5)) is GameState)
    }

    fun testTryRotation() {
        assert(defaultGame.tryRotation(Rotation.Left)?.pieceState?.rotation == Rotation.Left)
        assert(defaultGame.tryRotation(Rotation.Right)?.tryRotation(Rotation.Half)?.pieceState?.rotation == Rotation.Half)
    }

    fun testGetDroppedPosition() {}

    fun testGetShadow() {}

    fun testDrop() {}

    fun testHardDrop() {}

    fun testEnsureEnoughPieces() {}

    fun testAddScore() {}

    fun testAddPieceToBoard() {}

    fun testGetNextPiece() {}

    fun testHoldPiece() {}

    fun testResetPosition() {}

    fun testResetLockDelay() {}

    fun testUseLockRotation() {}

    fun testUseLockMovement() {}

    fun testPause() {}

    fun testResume() {}
}