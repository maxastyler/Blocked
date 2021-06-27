package com.maxtyler.blocked.repository

import android.content.Context
import com.google.android.gms.common.GoogleApiAvailability
import javax.inject.Inject

class PlayGamesRepository @Inject constructor(private val context: Context) {

    private val googleApiAvailability: GoogleApiAvailability = GoogleApiAvailability.getInstance()

    val playGamesAvailable: Int
        get() = googleApiAvailability.isGooglePlayServicesAvailable(context)
}