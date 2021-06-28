package com.maxtyler.blocked.ui

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxtyler.blocked.repository.PlayGamesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScoreTestViewModel @Inject constructor(private val playGamesRepository: PlayGamesRepository) :
    ViewModel() {

    private val _intent: MutableStateFlow<PendingIntent?> = MutableStateFlow(null)
    val intent = _intent.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("GAMES", "Hiyi")
//            playGamesRepository.setUpLeaderboardsClient()
//            try {Log.d("GAMES", "${playGamesRepository.submitScore(200)}")} catch (e: ApiException) {}
//            playGamesRepository.getHighScores()?.get()?.scores?.forEach {
//                Log.d("GAMES", "${it.displayScore}")
        }
    }

    fun signInClient() {
        viewModelScope.launch {
            _intent.value = playGamesRepository.fullSignIn()
//            playGamesRepository.setUpLeaderboardsClient()
//            playGamesRepository.getSignInIntent()?.let {
//                _intent.value = it
//            }
        }
    }
}
