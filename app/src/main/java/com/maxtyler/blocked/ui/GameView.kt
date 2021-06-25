package com.maxtyler.blocked.ui

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.blocked.game.BoardView
import com.example.blocked.game.DrawPiece
import com.maxtyler.blocked.database.Score
import com.maxtyler.blocked.game.GameState
import com.maxtyler.blocked.game.Piece
import com.maxtyler.blocked.game.Rotation
import com.maxtyler.blocked.game.Vec2
import java.time.Instant
import java.util.*

@Composable
fun ScoreView(score: Score) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Score: ${score.score}", modifier = Modifier.align(Alignment.CenterVertically))
        Text(score.date.toString(), modifier = Modifier.align(Alignment.CenterVertically))
    }
}

@Preview
@Composable
fun ScoreViewPreview() {
    ScoreView(score = Score(score = 10, date = Date.from(Instant.now())))
}

@Composable
fun GameOverView(viewModel: GameViewModel) {
    val listState = rememberLazyListState()
    val scores by viewModel.getScores().collectAsState(initial = listOf())
    val state by viewModel.gameState.collectAsState()
    Card(modifier = Modifier.padding(20.dp), elevation = 3.dp) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "GAME OVER", modifier = Modifier.align(Alignment.CenterVertically))
                Button(
                    onClick = { viewModel.startGame() },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Text("Restart Game")
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text("Your score:")
            Spacer(modifier = Modifier.height(4.dp))
            ScoreView(score = Score(score = state.score.score, date = Date.from(Instant.now())))

            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "High Scores:")
            Spacer(modifier = Modifier.height(5.dp))
            LazyColumn(state = listState) {
                items(scores) {
                    ScoreView(score = it)
                }
            }
        }
    }
}

@Composable
fun HeldPiece(piece: Piece?) {
    Column {
        Text("Held:", modifier = Modifier.align(Alignment.CenterHorizontally))
        Box(
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            piece?.let {
                DrawPiece(piece = it)
            }
        }
    }
}

@Composable
fun NextPieces(pieces: List<Piece>) {
    Column {
        Text("Next:", modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(10.dp))
        pieces.forEach {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.CenterHorizontally)
            ) { DrawPiece(piece = it) }
        }
    }
}

@Composable
fun GameView(viewModel: GameViewModel = viewModel()) {
    RunFunctionOnPauseAndResume(onPause = {
        viewModel.pause()
        if (viewModel.gameState.value.mode != GameState.Mode.GameOver) viewModel.saveState()
    }, onResume = { })
    val state by viewModel.gameState.collectAsState()
    var offset by remember { mutableStateOf(Offset(0F, 0F)) }
    var dropping by remember { mutableStateOf(false) }
    var alreadyDropped by remember { mutableStateOf(false) }
    val dragAmount = 50F
    val dropAmount = 200F
    val hardDropAmount = 130F

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.mode == GameState.Mode.GameOver) {
            Box(modifier = Modifier.align(Alignment.Center)) {
                GameOverView(viewModel = viewModel)
            }
        } else {
            Box(modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(onDragStart = {
                        offset = Offset(0F, 0F)
                        dropping = false
                        alreadyDropped = false
                    },
                        onDrag = { change, amount ->
                            if (state.mode == GameState.Mode.Playing) {
                                offset += amount
                                if (amount.y > hardDropAmount) {
                                    if (!alreadyDropped) {
                                        viewModel.hardDrop()
                                        alreadyDropped = true
                                    }
                                } else {
                                    val xDragAmount = if (dropping) 3 * dragAmount else dragAmount
                                    while (offset.x > xDragAmount) {
                                        offset = offset.copy(x = offset.x - xDragAmount)
                                        viewModel.move(Vec2(1, 0))
                                    }
                                    while (offset.x < -xDragAmount) {
                                        offset = offset.copy(x = offset.x + xDragAmount)
                                        viewModel.move(Vec2(-1, 0))
                                    }
                                    if (!alreadyDropped) {
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
            Column {
                Card(elevation = 3.dp, modifier = Modifier.padding(10.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                            Text("Score: ${state.score.score}")
                            Text("Level: ${state.score.level}")
                        }
                        if (state.mode == GameState.Mode.Playing) {
                            Button(
                                onClick = { viewModel.pause() },
                                modifier = Modifier.align(Alignment.CenterVertically)
                            ) {
                                Text("Pause")
                            }
                        }
                    }
                }
                Row(modifier = Modifier.padding(10.dp)) {
                    Card(
                        modifier = Modifier
                            .padding(5.dp)
                            .align(Alignment.CenterVertically),
                        elevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Bottom)
                                .fillMaxWidth(0.15F)
                                .padding(4.dp)
                        ) {
                            HeldPiece(piece = state.held)
                            Spacer(modifier = Modifier.height(20.dp))
                            NextPieces(pieces = state.pieces.take(6))
                        }
                    }
                    Box(modifier = Modifier.align(Alignment.CenterVertically)) { BoardView(state) }
                }
            }
            if (state.mode == GameState.Mode.Paused) {
                Box(modifier = Modifier.align(Alignment.Center)) {
                    PauseMenu(onResume = { viewModel.resume() },
                        onNewGame = { viewModel.restart() })
                }
            }
        }
    }

}

@Composable
fun PauseMenu(onResume: () -> Unit, onNewGame: () -> Unit) {
    Column {
        Button(onClick = onResume, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Resume")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onNewGame, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("New Game")
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
    Box {
        Text(piece.toString(), modifier = Modifier.align(Alignment.Center))
    }
}