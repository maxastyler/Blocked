package com.example.blocked.ui

import android.os.Vibrator
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blocked.database.Score
import com.example.blocked.game.GameState
import com.example.blocked.game.Rotation
import com.example.blocked.game.Vec2
import com.example.blocked.repository.ScoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.*
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val scoreRepository: ScoreRepository,
    private val vibrator: Vibrator
) :
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
                    if (newState.mode == GameState.Mode.GameOver) {
                        submitScore()
                    }
                }
            }
        }
    }

    fun pause() {
        if (gameState.value.mode == GameState.Mode.Playing) {
            _gameState.value = _gameState.value.pause()
        }
    }

    fun resume() {
        if (gameState.value.mode == GameState.Mode.Paused && !pauseTimer.started) {
            pauseTimer.start(0L)
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

    fun getScores(): Flow<List<Score>> = scoreRepository.getScores(10).flowOn(Dispatchers.IO)

    fun submitScore() {
        viewModelScope.launch(Dispatchers.IO) {
            scoreRepository.addScore(
                Score(
                    score = gameState.value.score.score,
                    date = Date.from(Instant.now())
                )
            )
        }
    }
}