package com.maxtyler.blocked.ui

import com.maxtyler.blocked.game.ColourSettings
import com.maxtyler.blocked.game.SettingType
import com.maxtyler.blocked.game.UISettings

data class TempUISettings(
    val settings: Map<String, String>,
    val colourSettings: Pair<Int?, ColourSettings>
) {
    constructor(uiSettings: UISettings) : this(
        settings = uiSettings.settings.mapValues { (_, v) -> v.value.toString() },
        colourSettings = uiSettings.colourSettings
    )

    sealed class ToUISettingsResult {
        data class Error(val errors: Set<String>) : ToUISettingsResult()
        data class Ok(val settings: Map<String, SettingType<out Any>>) : ToUISettingsResult()
    }

    fun toUISettings(): ToUISettingsResult {
        val verified = this.settings.mapValues { (k, v) -> UISettings.verifyString(k, v) }
        val errors = verified.filter { (_, v) -> v == null }.map { (k, _) -> k }.toSet()
        return if (errors.size > 0) {
            ToUISettingsResult.Error(errors)
        } else {
            ToUISettingsResult.Ok(verified.mapValues { (_, v) -> v!! })
        }
    }

    fun updateSetting(newPair: Pair<String, String>): TempUISettings =
        if (newPair.first in this.settings) this.copy(settings = this.settings + mapOf(newPair)) else this
}

