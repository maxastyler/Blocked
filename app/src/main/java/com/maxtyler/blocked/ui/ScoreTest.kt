package com.maxtyler.blocked.ui

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.GamesActivityResultCodes

@Composable
fun ScoreTestMain(viewModel: ScoreTestViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    LaunchedEffect(key1 = Unit) {
        viewModel.signInClient()
    }
    val inte by viewModel.intent.collectAsState()
    Column() {
        Text(text = "Hiyi")
        inte?.let {
            ActivityL(intent = it)
        }
    }
}

@Composable
fun ActivityL(intent: PendingIntent) {
    val laun =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) {
            val t = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            t.addOnCompleteListener { Log.d("GAMES", "Got signed in account result ${it.getResult().email}") }

        }
    Button(onClick = {
        Log.d("GAMES", "${intent.creatorUid}")
        laun.launch(IntentSenderRequest.Builder(intent).build()) }) {
        Text("LAUNCH!")
    }
}