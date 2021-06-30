package com.maxtyler.blocked.repository

import com.maxtyler.blocked.database.BlockedDatabase
import com.maxtyler.blocked.database.Save
import com.maxtyler.blocked.database.SaveDatabase
import com.maxtyler.blocked.game.GameState
import com.maxtyler.blocked.game.Score
import javax.inject.Inject

class SaveRepository @Inject constructor(private val saveDatabase: BlockedDatabase) {
    suspend fun clear() {
        saveDatabase.saveDao().delete()
    }

    suspend fun saveState(state: GameState) {
        val save = Save(
            width = state.board.width,
            height = state.board.height,
            board = state.board.blocks,
            score = state.score.score,
            lastClearWasTetris = state.score.lastClearWasTetris,
            level = state.score.level,
            levelStartScore = state.score.levelStartScore,
            position = state.pieceState.position,
            rotation = state.pieceState.rotation,
            piece = state.pieceState.piece,
            pieces = state.pieces,
            held = state.held,
            holdUsed = state.holdUsed
        )
        saveDatabase.saveDao().insert(save)
    }

    suspend fun getState(): GameState? = saveDatabase.saveDao().get()?.let {
        val state = GameState.fromWidthAndHeight(width = it.width, height = it.height)
        state.copy(
            board = state.board.copy(blocks = it.board),
            score = Score(
                it.score,
                it.lastClearWasTetris,
                it.level,
                it.levelStartScore
            ),
            pieceState = state.pieceState.copy(
                piece = it.piece,
                position = it.position,
                rotation = it.rotation
            ),
            pieces = it.pieces,
            held = it.held,
            holdUsed = it.holdUsed
        )
    }
}