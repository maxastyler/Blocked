package com.maxtyler.blocked.ui

import android.text.format.DateFormat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.blocked.game.BoardView
import com.example.blocked.game.DrawPiece
import com.maxtyler.blocked.database.Score
import com.maxtyler.blocked.game.*
import kotlinx.coroutines.flow.collectLatest
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
        Text(
            DateFormat.format("E d-M-y", score.date).toString(),
            modifier = Modifier.align(Alignment.CenterVertically)
        )
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
        Column(
            modifier = Modifier.padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
            if (viewModel.playGamesAvailable) {
                PlayGamesView(viewModel)
                Spacer(modifier = Modifier.height(10.dp))
            }
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
fun HeldPiece(piece: Piece?, colourSettings: ColourSettings) {
    Column {
        Text("Held:", modifier = Modifier.align(Alignment.CenterHorizontally))
        Box(
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            piece?.let {
                DrawPiece(piece = it, colourSettings)
            }
        }
    }
}

@Composable
fun NextPieces(pieces: List<Piece>, colourSettings: ColourSettings) {
    Column {
        Text("Next:", modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(10.dp))
        pieces.forEach {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.CenterHorizontally)
            ) { DrawPiece(piece = it, colourSettings) }
        }
    }
}

@Composable
fun GameScaffold(viewModel: GameViewModel = viewModel(), navController: NavController) {
    val scaffoldState = rememberScaffoldState()
    LaunchedEffect(key1 = Unit) {
        viewModel.snackbarChannel.collectLatest {
            scaffoldState.snackbarHostState.showSnackbar(it)
        }
    }
    Scaffold(scaffoldState = scaffoldState,
        snackbarHost = { SnackbarHost(hostState = it) }) {
        GameView(viewModel, navController = navController)
    }
}

@Composable
fun GameView(viewModel: GameViewModel, navController: NavController) {
    RunFunctionOnPauseAndResume(onPause = {
        viewModel.pause()
        if (viewModel.gameState.value.mode != GameState.Mode.GameOver) viewModel.saveState()
    }, onResume = { })
    val state by viewModel.gameState.collectAsState()
    var offset by remember { mutableStateOf(Offset(0F, 0F)) }
    var dropping by remember { mutableStateOf(false) }
    var alreadyDropped by remember { mutableStateOf(false) }
    val settings by viewModel.uiSettings.collectAsState(initial = UISettings())
    val colourSettings = settings.colourSettings.second

    val hardDropLimit = settings.settings["hardDropLimit"]!!.value as Float
    val dropHorizontalMultiplier = settings.settings["dropHorizontalMultiplier"]!!.value as Float
    val dragLimit = settings.settings["dragLimit"]!!.value as Float
    val holdLimit = settings.settings["holdLimit"]!!.value as Float

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
                                if (amount.y > hardDropLimit) {
                                    if (!alreadyDropped) {
                                        viewModel.hardDrop()
                                        alreadyDropped = true
                                    }
                                } else {
                                    val xDragAmount =
                                        if (dropping) dropHorizontalMultiplier * dragLimit else dragLimit
                                    while (offset.x > xDragAmount) {
                                        offset = offset.copy(x = offset.x - xDragAmount)
                                        viewModel.move(Vec2(1, 0))
                                    }
                                    while (offset.x < -xDragAmount) {
                                        offset = offset.copy(x = offset.x + xDragAmount)
                                        viewModel.move(Vec2(-1, 0))
                                    }
                                    if (!alreadyDropped) {
                                        while (offset.y > dragLimit) {
                                            dropping = true
                                            offset = offset.copy(y = offset.y - dragLimit)
                                            if (viewModel.drop(false)) alreadyDropped = true
                                        }
                                        if (offset.y < -holdLimit) {
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
                            HeldPiece(piece = state.held, colourSettings)
                            Spacer(modifier = Modifier.height(20.dp))
                            NextPieces(pieces = state.pieces.take(6), colourSettings)
                        }
                    }
                    Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                        BoardView(
                            state,
                            colourSettings
                        )
                    }
                }
            }
            if (state.mode == GameState.Mode.Paused) {
                Box(modifier = Modifier.align(Alignment.Center)) {
                    PauseMenu(
                        onResume = { viewModel.resume() },
                        onNewGame = { viewModel.restart() },
                        onSettings = { navController.navigate("settings") },
                        viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun PlayGamesView(
    viewModel: GameViewModel
) {
    val player by viewModel.player.collectAsState()
    val leaderboardIntent by viewModel.leaderboardIntent.collectAsState()
    val signInIntentLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            viewModel.handleSignInActivityResult(it)
        }
    val leaderboardIntentLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
        }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        player?.let {
            Text("Signed in as: ${it.displayName}")
        }
        Spacer(modifier = Modifier.height(3.dp))
        if (leaderboardIntent != null) {
            Button(onClick = { leaderboardIntentLauncher.launch(leaderboardIntent) }) {
                Text("View online leaderboards")
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            if (player == null) {
                Button(onClick = { signInIntentLauncher.launch(viewModel.signInIntent) }) {
                    Text("Log in")
                }
            } else {
                Button(onClick = { viewModel.signOut() }) {
                    Text("Sign out")
                }
                Spacer(modifier = Modifier.width(20.dp))
                Button(onClick = { viewModel.revokeAccess() }) {
                    Text("Revoke access")
                }
            }
        }
    }
}

@Composable
fun PauseMenu(
    onResume: () -> Unit,
    onNewGame: () -> Unit,
    onSettings: () -> Unit,
    vm: GameViewModel
) {

    Card(backgroundColor = Color.LightGray.copy(alpha = 0.5F)) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = onResume, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Resume")
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onSettings, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Settings")
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onNewGame, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("New Game")
            }
            Spacer(modifier = Modifier.height(30.dp))
            if (vm.playGamesAvailable) {
                PlayGamesView(vm)
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
    Box {
        Text(piece.toString(), modifier = Modifier.align(Alignment.Center))
    }
}