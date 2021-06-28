package com.maxtyler.blocked.repository

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.games.AnnotatedData
import com.google.android.gms.games.Games
import com.google.android.gms.games.LeaderboardsClient
import com.google.android.gms.games.leaderboard.LeaderboardVariant
import com.google.android.gms.games.leaderboard.ScoreSubmissionData
import com.maxtyler.blocked.R
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PlayGamesRepository @Inject constructor(private val context: Context) {

    private val googleApiAvailability: GoogleApiAvailability = GoogleApiAvailability.getInstance()

    private val googleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build()

    private var _signInClient: GoogleSignInClient? = null
    val signInClient: GoogleSignInClient?
        get() {
            if (_signInClient == null) _signInClient =
                GoogleSignIn.getClient(context, googleSignInOptions)
            return _signInClient
        }

    val playGamesAvailable: Int
        get() = googleApiAvailability.isGooglePlayServicesAvailable(context)


}