package com.maxtyler.blocked.repository

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.games.Games
import com.google.android.gms.games.Player
import com.maxtyler.blocked.R
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PlayGamesRepository @Inject constructor(private val context: Context) {

    private val googleApiAvailability: GoogleApiAvailability = GoogleApiAvailability.getInstance()

    private val googleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build()

    private var _signInClient: GoogleSignInClient? = null
    val signInClient: GoogleSignInClient
        get() {
            if (_signInClient == null) _signInClient =
                GoogleSignIn.getClient(context, googleSignInOptions)
            return _signInClient!!
        }

    val playGamesAvailable: Int
        get() = googleApiAvailability.isGooglePlayServicesAvailable(context)

    suspend fun signOut() {
        signInClient.revokeAccess().await()
        signInClient.signOut().await()
    }

    suspend fun silentSignIn(): GoogleSignInAccount? {
        try {
            val lastAccount = GoogleSignIn.getLastSignedInAccount(context)
            if (lastAccount != null) {
                return lastAccount
            }
            return signInClient.silentSignIn().await()
        } catch (e: ApiException) {
            return null
        }
    }

    suspend fun signInIntent(): Intent {
        return signInClient.signInIntent
    }

    suspend fun getCurrentPlayer(account: GoogleSignInAccount): Player {
        return Games.getPlayersClient(context, account).currentPlayer.await()
    }

    suspend fun getLeaderboard(account: GoogleSignInAccount): Intent =
        Games.getLeaderboardsClient(context, account)
            .getLeaderboardIntent(context.resources.getString(R.string.leaderboard_id)).await()

    suspend fun submitScore(account: GoogleSignInAccount, score: Long) {
        Games.getLeaderboardsClient(context, account)
            .submitScoreImmediate(context.resources.getString(R.string.leaderboard_id), score)
            .await()
    }
}