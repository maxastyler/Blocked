package com.maxtyler.blocked.ui

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.games.Player
import com.maxtyler.blocked.repository.PlayGamesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScoreTestViewModel @Inject constructor(private val playGamesRepository: PlayGamesRepository) :
    ViewModel() {

    private val _intent: MutableStateFlow<Intent?> = MutableStateFlow(null)
    val intent = _intent.asStateFlow()

    private val _account: MutableStateFlow<GoogleSignInAccount?> = MutableStateFlow(null)
    val account = _account.asStateFlow()

    private val _player: MutableStateFlow<Player?> = MutableStateFlow(null)
    val player = _player.asStateFlow()

    private val _leaderboardIntent: MutableStateFlow<Intent?> = MutableStateFlow(null)
    val leaderboardIntent = _leaderboardIntent.asStateFlow()

    init {
        silentSignIn()
        viewModelScope.launch {
            _account.collect() {
                when (it) {
                    null -> {
                        _player.value = null
                        _leaderboardIntent.value = null
                    }
                    else -> {
                        _player.value = playGamesRepository.getCurrentPlayer(it)
                        _leaderboardIntent.value = playGamesRepository.getLeaderboard(it)
                    }
                }
            }
        }
    }

    fun silentSignIn() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val acc = playGamesRepository.silentSignIn()) {
                null -> getSignInIntent()
                else -> setAccount(acc)
            }
        }
    }

    fun signOut() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                playGamesRepository.signOut()
                _account.value = null
            } catch (e: ApiException) {
                Log.d(
                    "GAMES",
                    "success: ${e.status?.isSuccess}\n${e.status?.connectionResult}\nStatus code string: ${
                        CommonStatusCodes.getStatusCodeString(e.statusCode)
                    }"
                )
            }
        }
    }

    fun getSignInIntent() {
        viewModelScope.launch(Dispatchers.IO) {
            _intent.value = playGamesRepository.signInIntent()
        }
    }

    fun setAccount(account: GoogleSignInAccount) {
        _account.value = account
    }

    fun getLeaderboardIntent(account: GoogleSignInAccount) {
        viewModelScope.launch {
            _leaderboardIntent.value = playGamesRepository.getLeaderboard(account)
        }
    }

    fun submitScore() {
        account.value?.let {
            viewModelScope.launch {
                playGamesRepository.submitScore(it, 8)
            }
        }
    }
}
