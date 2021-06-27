package com.maxtyler.blocked.ui

import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxtyler.blocked.BuildConfig
import com.maxtyler.blocked.database.Score
import com.maxtyler.blocked.game.GameState
import com.maxtyler.blocked.game.Rotation
import com.maxtyler.blocked.game.Vec2
import com.maxtyler.blocked.repository.PlayGamesRepository
import com.maxtyler.blocked.repository.SaveRepository
import com.maxtyler.blocked.repository.ScoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.util.*
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val scoreRepository: ScoreRepository,
    private val saveRepository: SaveRepository,
    private val playGamesRepository: PlayGamesRepository,
    private val vibrator: Vibrator,
) :
    ViewModel() {
    private var _gameState: MutableStateFlow<GameState> =
        MutableStateFlow(GameState.fromWidthAndHeight(10, 30))
    private val lockTimer = Timer(viewModelScope)
    private val gravityTimer = Timer(viewModelScope)
    private val pauseTimer = Timer(viewModelScope)
    private val saveScope = CoroutineScope(viewModelScope.coroutineContext)
    private var vibrationJob: Job? = null
    private val moveVibrationEffect = VibrationEffect.createOneShot(10L, 30)
    private val dropVibrationEffect = VibrationEffect.createOneShot(100L, 200)
    private var scoreSubmitted = false
    val gameState = _gameState.asStateFlow()

    init {
        Log.d("GAMES", "${playGamesRepository.playGamesAvailable}")
        viewModelScope.launch {
            lockTimer.events.collect { drop(false) }
        }
        viewModelScope.launch {
            gravityTimer.events.collect { drop(true) }
        }
        viewModelScope.launch {
            pauseTimer.events.collect {
                gravityTimer.start(_gameState.value.score.dropTime, true)
                _gameState.value = _gameState.value.resume()
            }
        }
        startGame()
        loadSavedState()
        pause()
    }

    fun startGame(width: Int = 10, height: Int = 30) {
        lockTimer.stop()
        _gameState.value =
            GameState.fromWidthAndHeight(width = width, height = height).resetPosition()
        gravityTimer.start(_gameState.value.score.dropTime, true)
        scoreSubmitted = false
    }

    fun restart() {
        startGame(gameState.value.board.width, gameState.value.board.height)
    }

    fun rotate(rotation: Rotation) {
        when (rotation) {
            Rotation.Right, Rotation.Left ->
                when (val newState =
                    gameState.value.tryRotation(gameState.value.pieceState.rotation + rotation)) {
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
        gameState.value.tryPosition(gameState.value.pieceState.position + direction)
            ?.run {
                vibrate(moveVibrationEffect)
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
        _gameState.value.let { gameState ->
            if (gameState.mode == GameState.Mode.Playing) {
                val (newState, locked) = when (val x = gameState.drop()) {
                    is GameState.Dropped -> Pair(x.gameState, false)
                    is GameState.AddPieceToBoardReturn -> {
                        if (x.gameOver && !computerDrop) onGameOver()
                        Pair(x.gameState, true)
                    }
                }

                // do the drop vibration if it was dropped by a player
                if (!computerDrop) vibrate(moveVibrationEffect)

                if (locked) {
                    if (computerDrop) {
                        if (!lockTimer.started) {
                            lockTimer.start(gameState.lockDelay.timeOut)
                        }
                    } else {
                        gravityTimer.start(newState.score.dropTime, true)
                        _gameState.value = newState
                    }
                } else {
                    lockTimer.stop()
                    _gameState.value = newState
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
            val (newGameState, gameOver, _) = gameState.hardDrop()
            if (gameOver) onGameOver()
            _gameState.value = newGameState
            vibrate(dropVibrationEffect)
        }
    }

    fun hold() {
        gameState.value.let { gameState ->
            if (!gameState.holdUsed) vibrate(moveVibrationEffect)
            _gameState.value = gameState.holdPiece().first
        }
    }

    /**
     * Do the things which need to happen on game over
     */
    fun onGameOver() {
        submitScore()
        clearSave()
        lockTimer.stop()
        gravityTimer.stop()
    }

    fun getScores(): Flow<List<Score>> = scoreRepository.getScores(10).flowOn(Dispatchers.IO)

    fun submitScore() {
        saveScope.launch(Dispatchers.IO) {
            supervisorScope {
                if (!scoreSubmitted) {
                    scoreSubmitted = true
                    scoreRepository.addScore(
                        Score(
                            score = gameState.value.score.score,
                            date = Date.from(Instant.now())
                        )
                    )
                }
            }
        }
    }

    fun loadSavedState() {
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

    fun vibrate(effect: VibrationEffect) {
        if (vibrationJob?.isActive != true) {
            vibrationJob = viewModelScope.launch(Dispatchers.Default) {
                vibrator.vibrate(effect)
            }
        }
    }
}