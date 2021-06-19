package com.example.blocked.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blocked.database.Score
import com.example.blocked.game.GameState
import com.example.blocked.game.Rotation
import com.example.blocked.game.Vec2
import com.example.blocked.repository.ScoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(private val scoreRepository: ScoreRepository) :
    ViewModel() {
    private var _gameState: MutableStateFlow<GameState> = MutableStateFlow(GameState(10, 30))
    private val lockTimer = Timer(viewModelScope)
    private val gravityTimer = Timer(viewModelScope)
    private val pauseTimer = Timer(viewModelScope)
    val gameState = _gameState.asStateFlow()

    init {
        viewModelScope.launch {
            lockTimer.events.collect { drop(false) }
        }
        viewModelScope.launch {
            gravityTimer.events.collect { drop(true) }
        }
        viewModelScope.launch {
            pauseTimer.events.collect { _gameState.value = _gameState.value.resume() }
        }
        startGame()
    }

    fun startGame(width: Int = 10, height: Int = 30) {
        _gameState.value = GameState(width = width, height = height).resetPosition()
        gravityTimer.start(_gameState.value.dropTime(), true)
    }

    fun rotate(rotation: Rotation) {
        when (rotation) {
            Rotation.Right, Rotation.Left ->
                when (val newState =
                    gameState.value.tryRotation(gameState.value.rotation + rotation)) {
                    is GameState -> {
                        val s = newState.useLockRotation()
                        _gameState.value = s
                        if (lockTimer.started) {
                            lockTimer.start(s.lockDelay.timeOut)
                        }
                    }
                }
        }
    }

    fun move(direction: Vec2) {
        gameState.value.tryPosition(gameState.value.position + direction)
            ?.run {
                val newState = this.useLockMovement()
                _gameState.value = newState
                if (lockTimer.started) {
                    lockTimer.start(newState.lockDelay.timeOut)
                }
            }
    }

    /**
     * Drop the piece, starting a lock timer if the piece was gravity dropped
     * @param computerDrop Whether the piece was gravity-dropped or not
     */
    fun drop(computerDrop: Boolean) {
        gameState.value.let { gameState ->
            when (gameState.mode) {
                GameState.Mode.Playing -> {
                    val (newState, locked)
                            = gameState.drop()
                    if (locked) {
                        if (computerDrop) {
                            if (!lockTimer.started) {
                                lockTimer.start(gameState.lockDelay.timeOut)
                            }
                        } else {
                            gravityTimer.start(newState.dropTime(), true)
                            _gameState.value = newState
                        }
                    } else {
                        lockTimer.stop()
                        _gameState.value = newState
                    }
                }
            }
        }
    }

    fun pause() {
        _gameState.value = _gameState.value.pause()
    }

    fun resume() {
        if (!pauseTimer.started) {
            pauseTimer.start(1000L)
        }
    }

    fun hardDrop() {
        gameState.value.let { gameState ->
            _gameState.value = gameState.hardDrop()
        }
    }

    fun hold() {
        gameState.value.let { gameState ->
            _gameState.value = gameState.holdPiece()
        }
    }
}