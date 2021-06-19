package com.example.blocked.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.blocked.database.Score
import com.example.blocked.game.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toCollection
import java.time.Instant
import java.util.*

@Composable
fun ScoreView(score: Score) {
    Card() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Score: ${score.score}")
            Text(score.date.toString())
        }
    }
}

@Preview
@Composable
fun ScoreViewPreview() {
    ScoreView(score = Score(id = 1, score = 10, date = Date.from(Instant.now())))
}

@Composable
fun GameOverView(viewModel: GameViewModel) {
    val listState = rememberLazyListState()
    val scores by viewModel.getScores().collectAsState(initial = listOf())
    Column() {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "GAME OVER", modifier = Modifier.align(Alignment.CenterVertically))
            Button(
                onClick = { viewModel.startGame() },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text("Restart Game")
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn(state = listState) {
            items(scores) {
                ScoreView(score = it)
            }
        }
    }
}

@Composable
fun GameView(viewModel: GameViewModel = viewModel()) {
    RunFunctionOnPauseAndResume(onPause = { viewModel.pause() }, onResume = { })
    val state by viewModel.gameState.collectAsState()
    var offset by remember { mutableStateOf(Offset(0F, 0F)) }
    var dropping by remember { mutableStateOf(false) }
    val dragAmount = 50F
    val dropAmount = 200F

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.mode == GameState.Mode.GameOver) {
            GameOverView(viewModel = viewModel)
        } else {
            Box(modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(onDragStart = {
                        offset = Offset(0F, 0F)
                        dropping = false
                    },
                        onDragEnd = { dropping = false },
                        onDrag = { change, amount ->
                            if (state.mode == GameState.Mode.Playing) {
                                offset += amount
                                if (!dropping) {
                                    while (offset.x > dragAmount) {
                                        offset = offset.copy(x = offset.x - dragAmount)
                                        viewModel.move(Vec2(1, 0))
                                    }
                                    while (offset.x < -dragAmount) {
                                        offset = offset.copy(x = offset.x + dragAmount)
                                        viewModel.move(Vec2(-1, 0))
                                    }
                                } else {
                                    val dragAmount = 3 * dragAmount
                                    while (offset.x > dragAmount) {
                                        offset = offset.copy(x = offset.x - dragAmount)
                                        viewModel.move(Vec2(1, 0))
                                    }
                                    while (offset.x < -dragAmount) {
                                        offset = offset.copy(x = offset.x + dragAmount)
                                        viewModel.move(Vec2(-1, 0))
                                    }
                                }
                                while (offset.y > dragAmount) {
                                    dropping = true
                                    offset = offset.copy(y = offset.y - dragAmount)
                                    viewModel.drop(false)
                                }
                                if (offset.y < -dropAmount) {
                                    viewModel.hold()
                                    offset = offset.copy(y = 0F)
                                }
                            }
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTapGestures { pos ->
                        if (state.mode == GameState.Mode.Playing) {
                            if (pos.x > size.width / 2) {
                                viewModel.rotate(Rotation.Right)
                            } else {
                                viewModel.rotate(Rotation.Left)
                            }
                        }
                    }
                })
            Column() {
                Card(elevation = 3.dp, modifier = Modifier.padding(10.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column() {
                            Text("Score: ${state.score.score}")
                            Text("Level: ${state.score.level}")
                        }
                        Text("Held piece: ${state.held}")
                    }
                }
                Row() {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Bottom)
                            .padding(4.dp)
                    ) {
                        state.pieces.drop(1).take(4).forEach {
                            PieceView(piece = it)
                        }
                    }
                    BoardView(state)
                }
            }
            if (state.mode == GameState.Mode.Paused) {
                Button(
                    onClick = { viewModel.resume() },
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text("Resume")
                }
            }
        }
    }

}

@Composable
fun RunFunctionOnPauseAndResume(onPause: () -> Unit, onResume: () -> Unit) {
    val pause by rememberUpdatedState(newValue = onPause)
    val resume by rememberUpdatedState(newValue = onResume)
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val observer = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_PAUSE -> pause()
            Lifecycle.Event.ON_RESUME -> resume()
            else -> Unit
        }
    }
    DisposableEffect(key1 = lifecycle) {
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
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