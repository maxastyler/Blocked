package com.example.blocked.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.blocked.game.*
import kotlinx.coroutines.delay

@Composable
fun GameView(viewModel: GameViewModel = viewModel()) {
    val state by viewModel.gameState.collectAsState()
    var offset by remember { mutableStateOf(Offset(0F, 0F)) }
    val dragAmount = 50F
    val dropAmount = 200F

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.mode == GameState.Mode.GameOver) {
            Column(modifier = Modifier.align(Alignment.Center)) {
                Text(text = "GAME OVER")
                Button(onClick = {viewModel.startGame()}) {
                    Text("Restart Game")
                }
            }
        } else {
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
                            while (offset.y > dragAmount) {
                                offset = offset.copy(y = offset.y - dragAmount)
                                viewModel.drop()
                            }
                            if (offset.y < -dropAmount) {
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
            Row() {
                BoardView(state)
                Column() {
                    state.pieces.drop(1).take(4).forEach {
                        PieceView(piece = it)
                    }
                }
            }
        }
    }

}

@Composable
fun PieceView(piece: Piece) {
    Box() {
        Text(piece.toString(), modifier = Modifier.align(Alignment.Center))
    }
}

@Preview
@Composable
fun GameViewPreview() {
    val state = GameState(width = 4, height = 5)
    GameView()
}