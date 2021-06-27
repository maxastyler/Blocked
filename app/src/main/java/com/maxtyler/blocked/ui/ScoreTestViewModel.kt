package com.maxtyler.blocked.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ApiException
import com.maxtyler.blocked.repository.PlayGamesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScoreTestViewModel @Inject constructor(private val playGamesRepository: PlayGamesRepository) :
    ViewModel() {

    private val _intent: MutableStateFlow<Intent?> = MutableStateFlow(null)
    val intent = _intent.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            playGamesRepository.setUpLeaderboardsClient()
            try {Log.d("GAMES", "${playGamesRepository.submitScore(200)}")} catch (e: ApiException) {}
            playGamesRepository.getHighScores()?.get()?.scores?.forEach {
                Log.d("GAMES", "${it.displayScore}")
            }
        }
    }

    fun signInClient() {
        viewModelScope.launch {
            playGamesRepository.getSignInIntent()?.let {
                _intent.value = it
            }
        }
    }
}
