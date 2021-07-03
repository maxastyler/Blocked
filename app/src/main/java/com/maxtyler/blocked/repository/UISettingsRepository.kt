package com.maxtyler.blocked.repository

import android.util.Log
import com.maxtyler.blocked.database.*
import com.maxtyler.blocked.game.ColourSettings
import com.maxtyler.blocked.game.SettingType
import com.maxtyler.blocked.game.UISettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UISettingsRepository @Inject constructor(private val database: BlockedDatabase) {
    private val settingsDao: SettingsDao = database.settingsDao()

    private fun settingToSettingType(setting: Setting): SettingType<out Any> {
        return when (setting.type) {
            SettingTypeValue.Int -> (UISettings.defaultSettings[setting.name] as SettingType.IntSetting).copy(
                value = setting.value.toInt()
            )
            SettingTypeValue.Float -> (UISettings.defaultSettings[setting.name] as SettingType.FloatSetting).copy(
                value = setting.value.toFloat()
            )
            SettingTypeValue.Long -> (UISettings.defaultSettings[setting.name] as SettingType.LongSetting).copy(
                value = setting.value.toLong()
            )
        }
    }

    private fun settingTypeToSetting(settingType: SettingType<out Any>): Setting {
        val x = when (settingType) {
            is SettingType.FloatSetting -> SettingTypeValue.Float
            is SettingType.LongSetting -> SettingTypeValue.Long
            is SettingType.IntSetting -> SettingTypeValue.Int
        }
        return Setting(settingType.name, type = x, value = settingType.value.toString())
    }


    fun getUISettings(): Flow<UISettings> =
        settingsDao.getSettings().map { it.map { it.name to settingToSettingType(it) }.toMap() }
            .combine(settingsDao.getColour()) { settingsMap, colours ->
                Log.d("GAMES", "Flow settings are: ${settingsMap}")
                UISettings.fromValuesAndDefaults(settingsMap,
                    colours?.let {
                        Pair(
                            it.colourChoice.colourSettingsId,
                            it.colours?.let { ColourSettingsRepository.coloursToColourSettings(it) }
                                ?: ColourSettings())
                    } ?: Pair(null, ColourSettings()))
            }

    suspend fun updateSettings(uiSettings: UISettings) {
        uiSettings.colourSettings.first?.run {
            settingsDao.insert(ColourChoice(colourSettingsId = this))
        }
        val settings = uiSettings.settings.map { (_, s) -> settingTypeToSetting(s) }
        settingsDao.insert(settings)
    }
}