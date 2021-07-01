package com.maxtyler.blocked.repository

import androidx.compose.ui.graphics.Color
import com.maxtyler.blocked.database.BlockedDatabase
import com.maxtyler.blocked.database.Colours
import com.maxtyler.blocked.database.ColoursDao
import com.maxtyler.blocked.game.ColourSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ColourSettingsRepository @Inject constructor(database: BlockedDatabase) {
    private val coloursDao: ColoursDao = database.coloursDao()

    fun getColourSettings(): Flow<List<Pair<Int, ColourSettings>>> = coloursDao.getColours().map {
        it.map {
            Pair(
                it.coloursId,
                coloursToColourSettings(it)
            )
        }
    }

    suspend fun deleteColourSettings(id: Int) = coloursDao.delete(id)

    companion object {
        fun coloursToColourSettings(colours: Colours) = ColourSettings(
            backgroundColour = Color(colours.backgroundColour),
            shadowColour = Color(colours.shadowColour),
            IColour = Color(colours.IColour),
            JColour = Color(colours.JColour),
            TColour = Color(colours.TColour),
            SColour = Color(colours.SColour),
            ZColour = Color(colours.ZColour),
            LColour = Color(colours.LColour),
            OColour = Color(colours.OColour),
        )
    }
}