package com.maxtyler.blocked.ui

import android.app.Activity
import android.content.Intent
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.games.Player
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
import kotlinx.coroutines.tasks.await
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

    // Game variables

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

    private val _snackbarChannel: MutableSharedFlow<String> = MutableSharedFlow()
    val snackbarChannel = _snackbarChannel.asSharedFlow()

    // Login variables

    val playGamesAvailable = playGamesRepository.playGamesAvailable
    private val _leaderboardIntent: MutableStateFlow<Intent?> = MutableStateFlow(null)
    private val _account: MutableStateFlow<GoogleSignInAccount?> = MutableStateFlow(null)
    private val _player: MutableStateFlow<Player?> = MutableStateFlow(null)

    val signInIntent: Intent
        get() = playGamesRepository.signInIntent
    val leaderboardIntent = _leaderboardIntent.asStateFlow()
    val player = _player.asStateFlow()

    init {
        if (playGamesAvailable) {
            silentSignIn()
            viewModelScope.launch {
                _account.collect {
                    when (it) {
                        null -> {
                            _player.value = null
                            _leaderboardIntent.value = null
                        }
                        else -> {
                            getLeaderboardIntent()
                            val player = playGamesRepository.getCurrentPlayer(it)
                            _player.value = player
                            _snackbarChannel.emit("Signed in as: ${player.displayName}")
                        }
                    }
                }
            }
        }

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
        if (_account.value != null) {
            submitScoreToPlayGames()
        }
        clearSave()
        lockTimer.stop()
        gravityTimer.stop()
    }

    fun getScores(): Flow<List<Score>> = scoreRepository.getScores(30).flowOn(Dispatchers.IO)

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

    fun silentSignIn() {
        viewModelScope.launch() {
            val acc = try {
                withContext(Dispatchers.IO) {
                    playGamesRepository.silentSignIn()
                }
            } catch (e: ApiException) {
                null
            }
            when (acc) {
                null -> {
                }
                else -> setAccount(acc)
            }
        }
    }

    fun revokeAccess() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    playGamesRepository.revokeAccess()
                    signOut()
                }
            } catch (e: ApiException) {
                _snackbarChannel.emit("Couldn't revoke access; API Error ${e.status.statusCode}")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                playGamesRepository.signOut()
                _account.value = null
                _snackbarChannel.emit("Signed out")
            } catch (e: ApiException) {
                _snackbarChannel.emit("There was an error while signing out")
            }
        }
    }

    fun setAccount(account: GoogleSignInAccount) {
        _account.value = account
    }

    fun getLeaderboardIntent() {
        _account.value?.let {
            viewModelScope.launch {
                _leaderboardIntent.value = playGamesRepository.getLeaderboard(it)
            }
        }
    }

    fun submitScoreToPlayGames() {
        val acc = _account.value
        when (acc) {
            null -> viewModelScope.launch { _snackbarChannel.emit("Can't submit score; Not logged in!") }
            else -> {
                viewModelScope.launch {
                    try {
                        withContext(Dispatchers.IO) {
                            playGamesRepository.submitScore(
                                acc,
                                this@GameViewModel.gameState.value.score.score.toLong()
                            )
                        }
                        _snackbarChannel.emit("Score submitted")
                    } catch (e: ApiException) {
                        _snackbarChannel.emit("Can't submit score; Have you got a connection? (API code ${e.status.statusCode})")
                    }
                }
            }
        }
    }

    fun handleSignInActivityResult(response: ActivityResult) {
        if (response.resultCode == Activity.RESULT_OK) {
            try {
                viewModelScope.launch(Dispatchers.IO) {
                    _account.value =
                        GoogleSignIn.getSignedInAccountFromIntent(response.data).await()
                }
            } catch (e: ApiException) {
                viewModelScope.launch {
                    _snackbarChannel.emit("Couldn't log in: API exception ${e.status.statusCode}")
                }
                _account.value = null
            } catch (e: NullPointerException) {
                viewModelScope.launch {
                    _snackbarChannel.emit("Null response received")
                }
            }
        } else {
            viewModelScope.launch {
                _snackbarChannel.emit("Bad result from login intent: ${ActivityResult.resultCodeToString(response.resultCode)}")
            }
        }
    }

}