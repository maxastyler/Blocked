package com.maxtyler.blocked.ui

import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import dagger.hilt.android.qualifiers.ApplicationContext

@Composable
fun ScoreTestMain(viewModel: ScoreTestViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    LaunchedEffect(key1 = Unit) {
        viewModel.signInClient()
    }
    val inte by viewModel.intent.collectAsState()

    val laun = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {

    }
    Text(text = "Hiyi")
    LaunchedEffect(key1 = inte) {
        Log.d("GAMES", "${inte}")
        if (inte != null) {
            laun.launch(inte)
        }
    }
}

@Composable
fun ActivityL(intent: Intent) {
    val laun = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
    }

}