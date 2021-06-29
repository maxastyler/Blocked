package com.maxtyler.blocked.ui

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.games.Player
import com.maxtyler.blocked.repository.PlayGamesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ScoreTestViewModel @Inject constructor(private val playGamesRepository: PlayGamesRepository) :
    ViewModel() {

    private val _toastChannel: MutableSharedFlow<String> = MutableSharedFlow()
    val toastChannel = _toastChannel.asSharedFlow()

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
                        getSignInIntent()
                    }
                    else -> {
                        val player = playGamesRepository.getCurrentPlayer(it)
                        _player.value = player
                        _leaderboardIntent.value = playGamesRepository.getLeaderboard(it)
                        _toastChannel.emit("Signed in as: ${player.name}")
                    }
                }
            }
        }
    }

    fun silentSignIn() {
        viewModelScope.launch(Dispatchers.IO) {
            val acc = try {
                playGamesRepository.silentSignIn()
            } catch (e: ApiException) {
                null
            }
            when (acc) {
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
                _toastChannel.emit("Signed out")
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
            try {
                viewModelScope.launch {
                    playGamesRepository.submitScore(it, 8)
                }
            } catch (e: ApiException) {

            }
        }
    }

    fun handleActivityResult(response: ActivityResult) {
        if (response.resultCode == Activity.RESULT_OK) {
            try {
                viewModelScope.launch(Dispatchers.IO) {
                    _account.value =
                        GoogleSignIn.getSignedInAccountFromIntent(response.data).await()
                }
            } catch (e: ApiException) {
                Log.d("GAMES", "Api exception ${e.status.statusCode}")
                _account.value = null
            }
        } else {
            Log.d(
                "GAMES",
                "Result not ok: ${ActivityResult.resultCodeToString(response.resultCode)}"
            )
        }
    }
}
