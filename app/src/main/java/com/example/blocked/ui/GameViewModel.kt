package com.example.blocked.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blocked.game.GameState
import com.example.blocked.game.Rotation
import com.example.blocked.game.Vec2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor() : ViewModel() {
    private var _gameState: MutableStateFlow<GameState> = MutableStateFlow(GameState(10, 30))
    private val dropTimer = Timer(viewModelScope)
    private val gravityTimer = Timer(viewModelScope)
    val gameState = _gameState.asStateFlow()

    init {
        startGame(10, 30)
        viewModelScope.launch {
            dropTimer.events.collect { drop() }
        }
        viewModelScope.launch {
            gravityTimer.events.collect { }
        }
        dropTimer.start(500L, true)
    }

    fun startGame(width: Int = 10, height: Int = 30) {
        _gameState.value = GameState(width = width, height = height)
    }

    fun rotate(rotation: Rotation) {
        when (rotation) {
            Rotation.Right, Rotation.Left ->
                when (val newState =
                    gameState.value.tryRotation(gameState.value.rotation + rotation)) {
                    is GameState -> _gameState.value = newState
                }
        }
    }

    fun move(direction: Vec2) {
        gameState.value.tryPosition(gameState.value.position + direction)
            ?.run { _gameState.value = this }
    }

    fun drop() {
        gameState.value.let { gameState ->
            _gameState.value = gameState.drop()
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