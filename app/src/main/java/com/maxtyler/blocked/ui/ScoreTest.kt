package com.maxtyler.blocked.ui

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ScoreTestMain(viewModel: ScoreTestViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {

    val inte by viewModel.intent.collectAsState()
    val account by viewModel.account.collectAsState()
    val player by viewModel.player.collectAsState()
    val leaderboardIntent by viewModel.leaderboardIntent.collectAsState()

    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = Unit) {
        viewModel.toastChannel.collectLatest { scaffoldState.snackbarHostState.showSnackbar(it) }
    }

    Scaffold(scaffoldState = scaffoldState,
        content = {

            Column() {
                Text(text = "Hiyi")
                player?.let {
                    Text(text = "Player: ${it.displayName}, ${it.playerId}")
                }

                account?.let {
                    Text(text = "Account: ${it.email} ${it.id}")
                    Button(onClick = { viewModel.submitScore() }) {
                        Text("Submit score")
                    }
                }
                inte?.let {
                    ActivityL(intent = it, viewModel)
                }
                Button(onClick = { viewModel.signOut() }) {
                    Text("Sign out")
                }
                leaderboardIntent?.let {
                    LeaderboardButton(intent = it)
                }
            }
        })
}

@Composable
fun LeaderboardButton(intent: Intent) {
    val l =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {

        }
    Button(onClick = { l.launch(intent) }) {
        Text("Show leaderboard")
    }
}

@Composable
fun ActivityL(intent: Intent, vm: ScoreTestViewModel) {
    val laun =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            vm.handleActivityResult(it)
        }
    val context = LocalContext.current
    Column() {
        Button(onClick = {
            laun.launch(intent)
            Toast.makeText(context, "hiyi!", Toast.LENGTH_SHORT).show()
        }) {
            Text("LAUNCH!")
        }
    }
}