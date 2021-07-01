package com.maxtyler.blocked.repository

import com.maxtyler.blocked.database.BlockedDatabase
import com.maxtyler.blocked.database.SettingsDao
import com.maxtyler.blocked.game.ColourSettings
import com.maxtyler.blocked.game.UISettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UISettingsRepository @Inject constructor(private val database: BlockedDatabase) {
    private val settingsDao: SettingsDao = database.settingsDao()

    fun getUISettings(): Flow<UISettings> = settingsDao.get().map { x ->
        when (x) {
            null -> UISettings()
            else -> UISettings(
                dragLimit = x.settings.dragLimit,
                holdLimit = x.settings.holdLimit,
                hardDropLimit = x.settings.hardDropLimit,
                dropHorizontalMultiplier = x.settings.dropHorizontalMultiplier,
                hardDropVibrationTime = x.settings.hardDropVibrationTime,
                hardDropVibrationStrength = x.settings.hardDropVibrationStrength,
                dropVibrationTime = x.settings.dropVibrationTime,
                dropVibrationStrength = x.settings.dropVibrationStrength,
                colourSettings = x.colours?.let {
                    Pair(it.coloursId, ColourSettingsRepository.coloursToColourSettings(it))
                } ?: Pair(null, ColourSettings())
            )
        }
    }
}