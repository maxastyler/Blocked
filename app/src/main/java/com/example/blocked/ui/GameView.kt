package com.example.blocked.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.blocked.game.*
import kotlinx.coroutines.delay

@Composable
fun GameView(viewModel: GameViewModel = viewModel()) {
    val state by viewModel.gameState.collectAsState()
    var offset by remember { mutableStateOf(Offset(0F, 0F)) }
    val dragAmount = 30F
    val dropAmount = 200F
    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            viewModel.drop()
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        if (state.mode == GameState.Mode.GameOver) {
            Text(text = "GAME OVER")
        }
        Box(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(onDragStart = { offset = Offset(0F, 0F) },
                    onDrag = { change, amount ->
                        offset += amount
                        while (offset.x > dragAmount) {
                            offset = offset.copy(x = offset.x - dragAmount)
                            viewModel.move(Vec2(1, 0))
                        }
                        while (offset.x < -dragAmount) {
                            offset = offset.copy(x = offset.x + dragAmount)
                            viewModel.move(Vec2(-1, 0))
                        }
                        while (offset.y < -dragAmount) {
                            offset = offset.copy(y = offset.y + dragAmount)
                            viewModel.drop()
                        }
                        if (offset.y > dropAmount) {
                            viewModel.hold()
                            offset = offset.copy(y = 0F)
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectTapGestures { pos ->
                    if (pos.x > size.width / 2) {
                        viewModel.rotate(Rotation.Right)
                    } else {
                        viewModel.rotate(Rotation.Left)
                    }
                }
            })
        BoardView(state)
    }
}


@Composable
fun PieceView(piece: Piece) {
    val coords = piece.getCoordinates(Rotation.None)
    Canvas(modifier = Modifier.fillMaxSize()) {
        val minX = coords.minByOrNull { it.x }
        val minY = coords.minByOrNull { it.y }

    }
}

@Preview
@Composable
fun GameViewPreview() {
    val state = GameState(width = 4, height = 5)
    GameView()
}