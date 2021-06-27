package com.maxtyler.blocked.repository

import android.content.Context
import android.content.Intent
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
    private var googleSignInAccount: GoogleSignInAccount? = null
    private var signInClient: GoogleSignInClient? = null
    private var leaderboardsClient: LeaderboardsClient? = null

    val playGamesAvailable: Int
        get() = googleApiAvailability.isGooglePlayServicesAvailable(context)

    fun getSignInClient() {
        signInClient = GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
    }

    fun setAccount(account: GoogleSignInAccount) {
        googleSignInAccount = account
        leaderboardsClient = Games.getLeaderboardsClient(context, account)
    }

    suspend fun setUpLeaderboardsClient() {
        getSignInClient()
        signInClient?.let {
            setAccount(it.silentSignIn().await())
        }
    }

    suspend fun getSignInIntent(): Intent? {
        getSignInClient()
        return signInClient?.let { it.signInIntent }
    }

    suspend fun submitScore(score: Long): ScoreSubmissionData? =
        leaderboardsClient?.submitScoreImmediate(
            context.resources.getString(R.string.leaderboard_id),
            score
        )?.await()

    suspend fun getHighScores(): AnnotatedData<LeaderboardsClient.LeaderboardScores>? =
        leaderboardsClient?.let {
            it.loadTopScores(
                context.resources.getString(R.string.leaderboard_id),
                LeaderboardVariant.TIME_SPAN_ALL_TIME,
                LeaderboardVariant.COLLECTION_PUBLIC,
                20,
                true
            ).await()
        }
//        googleSignInAccount?.let {
//        Log.d("GAMES", "gottit, ${it.id}")
//        Log.d("GAMES", "${it.grantedScopes} ${it.email}")
//        val t = Games.getLeaderboardsClient(context, it).loadTopScores(
//            context.resources.getString(R.string.leaderboard_id),
//            LeaderboardVariant.TIME_SPAN_ALL_TIME,
//            LeaderboardVariant.COLLECTION_PUBLIC,
//            10
//        )
//        t.addOnSuccessListener {
//            Log.d("GAMES", "Hiyi there")
//            val res = t.result
//            Log.d("GAMES", "LEADERBOARD: ${res.get().scores.count}")
//        }
//        t.addOnCanceledListener { Log.d("GAMES", "Cancelled") }
//        t.addOnCompleteListener { Log.d("GAMES", "Completed") }
//        t.addOnFailureListener { Log.d("GAMES", "Failed\n${it.message}") }
//        val p = Games.getLeaderboardsClient(context, it)
//        val myTask = Games.getLeaderboardsClient(context, it)
//            .submitScoreImmediate(context.resources.getString(R.string.leaderboard_id), 10)
//        myTask.addOnCompleteListener { Log.d("GAMES", "SUBMISSION COMPLETED") }
//        myTask.addOnSuccessListener { Log.d("GAMES", "SUBMISSION SUCCESS") }
//        myTask.addOnFailureListener { Log.d("GAMES", "SUBMISSION FAILED\n${it.message}") }
//        Log.d("GAMES", "LEADERBOARD ID: ${context.resources.getString(R.string.leaderboard_id)}")
//    }
}