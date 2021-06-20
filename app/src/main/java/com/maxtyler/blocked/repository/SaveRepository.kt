package com.maxtyler.blocked.repository

import com.example.blocked.database.Save
import com.example.blocked.database.SaveDatabase
import com.example.blocked.game.GameState
import javax.inject.Inject

class SaveRepository @Inject constructor(private val saveDatabase: SaveDatabase) {
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
            position = state.position,
            rotation = state.rotation,
            pieces = state.pieces,
            held = state.held,
            holdUsed = state.holdUsed
        )
        saveDatabase.saveDao().insert(save)
    }

    suspend fun getState(): GameState? = saveDatabase.saveDao().get().let {
        val state = GameState(width = it.width, height = it.height)
        state.copy(
            board = state.board.copy(blocks = it.board),
            score = GameState.Score(
                it.score,
                it.lastClearWasTetris,
                it.level,
                it.levelStartScore
            ),
            position = it.position,
            rotation = it.rotation,
            pieces = it.pieces,
            held = it.held,
            holdUsed = it.holdUsed
        )
    }
}