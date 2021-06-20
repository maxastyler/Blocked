package com.maxtyler.blocked.ui

import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxtyler.blocked.database.Score
import com.maxtyler.blocked.game.GameState
import com.maxtyler.blocked.game.Rotation
import com.maxtyler.blocked.game.Vec2
import com.maxtyler.blocked.repository.SaveRepository
import com.maxtyler.blocked.repository.ScoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.time.Instant
import java.util.*
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val scoreRepository: ScoreRepository,
    private val saveRepository: SaveRepository,
    private val vibrator: Vibrator,
) :
    ViewModel() {
    private var _gameState: MutableStateFlow<GameState> = MutableStateFlow(GameState(10, 30))
    private val lockTimer = Timer(viewModelScope)
    private val gravityTimer = Timer(viewModelScope)
    private val pauseTimer = Timer(viewModelScope)
    private val saveScope = CoroutineScope(viewModelScope.coroutineContext)
    private val vibrationEffect = VibrationEffect.createOneShot(10L, 150)
    val gameState = _gameState.asStateFlow()

    init {
        viewModelScope.launch {
            lockTimer.events.collect { drop(false) }
        }
        viewModelScope.launch {
            gravityTimer.events.collect { drop(true) }
        }
        viewModelScope.launch {
            pauseTimer.events.collect {
                gravityTimer.start(_gameState.value.dropTime(), true)
                _gameState.value = _gameState.value.resume()
            }
        }
        startGame()
        viewModelScope.launch(context = Dispatchers.IO) {
            try {
                val state = saveRepository.getState()
                state?.run {
                    _gameState.value = this.pause()
                }
            } catch (e: NumberFormatException) {
                saveRepository.clear()
            }
        }
        pause()
    }

    fun startGame(width: Int = 10, height: Int = 30) {
        lockTimer.stop()
        _gameState.value = GameState(width = width, height = height).resetPosition()
        gravityTimer.start(_gameState.value.dropTime(), true)
    }

    fun restart() {
        startGame(gameState.value.board.width, gameState.value.board.height)
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
                        clearSave()
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

    fun saveState() {
        saveScope.launch(Dispatchers.IO) {
            supervisorScope {
                saveRepository.saveState(gameState.value)
            }
        }
    }

    fun clearSave() {
        saveScope.launch(Dispatchers.IO) {
            supervisorScope {
                saveRepository.clear()
            }
        }
    }

    fun vibrate() {
        viewModelScope.launch(Dispatchers.Default) {
            vibrator.cancel()
            vibrator.vibrate(vibrationEffect)
        }
    }
}