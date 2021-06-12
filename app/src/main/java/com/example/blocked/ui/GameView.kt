package com.example.blocked.ui

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import com.example.blocked.game.BoardView
import com.example.blocked.game.GameState
import com.example.blocked.game.Rotation
import com.example.blocked.game.Vec2

@Composable
fun GameView() {
    var state by remember { mutableStateOf(GameState(5, 5)) }
    var offset by remember { mutableStateOf(Offset(0F, 0F)) }
    val dragAmount = 30F
    val dropAmount = 200F
    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectDragGestures(onDragStart = { offset = Offset(0F, 0F) },
                onDrag = { change, amount ->
                    offset += amount
                    while (offset.x > dragAmount) {
                        offset = offset.copy(x = offset.x - dragAmount)
                        state
                            .tryPosition(state.position + Vec2(1, 0))
                            ?.let { state = it }
                    }
                    while (offset.x < -dragAmount) {
                        offset = offset.copy(x = offset.x + dragAmount)
                        state
                            .tryPosition(state.position + Vec2(-1, 0))
                            ?.let { state = it }
                    }
                    while (offset.y < -dragAmount) {
                        offset = offset.copy(y = offset.y + dragAmount)
                        state = state.drop()
                    }
                    if (offset.y > dropAmount) {
                        state = state.hardDrop()
                        offset = offset.copy(y = 0F)
                    }
                }
            )
        }
        .pointerInput(Unit) {
            detectTapGestures { pos ->
                if (pos.x > size.width / 2) {
                    state.tryRotation(state.rotation + Rotation.Right)?.let{state = it}
                } else {
                    state.tryRotation(state.rotation + Rotation.Left)?.let{state = it}
                }
            }
        })
    BoardView(state)
}

@Preview
@Composable
fun GameViewPreview() {
    val state = GameState(width = 4, height = 5)
    GameView()
}