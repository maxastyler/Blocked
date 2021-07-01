package com.maxtyler.blocked.repository

import com.maxtyler.blocked.database.*
import com.maxtyler.blocked.game.ColourSettings
import com.maxtyler.blocked.game.SettingType
import com.maxtyler.blocked.game.UISettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UISettingsRepository @Inject constructor(private val database: BlockedDatabase) {
    private val settingsDao: SettingsDao = database.settingsDao()

    private fun settingToSettingType(setting: Setting): SettingType<out Number> =
        when (setting.type) {
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

    private fun settingTypeToSetting(settingType: SettingType<out Number>): Setting {
        val x = when (settingType) {
            is SettingType.FloatSetting -> SettingTypeValue.Float
            is SettingType.LongSetting -> SettingTypeValue.Long
            is SettingType.IntSetting -> SettingTypeValue.Int
        }
        return Setting(settingType.name, type = x, value = settingType.value.toString())
    }

    fun getUISettings(): Flow<UISettings> = settingsDao.get().map { x ->
        when (x) {
            null -> {
                UISettings()
            }
            else -> {
                UISettings.fromValuesAndDefaults(
                    x.settings.map { it.name to settingToSettingType(it) }.toMap(),
                    x.colours?.let {
                        Pair(it.coloursId, ColourSettingsRepository.coloursToColourSettings(it))
                    } ?: Pair(null, ColourSettings())
                )
            }
        }
    }

    suspend fun updateSettings(uiSettings: UISettings) {
        uiSettings.colourSettings.first?.run {
            settingsDao.insert(ColourChoice(colourSettingsId = this))
        }
        settingsDao.insert(uiSettings.settings.map { (_, setting) -> settingTypeToSetting(setting) })
    }
}