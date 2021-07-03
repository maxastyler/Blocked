package com.maxtyler.blocked.ui

import com.maxtyler.blocked.game.UISettings
import junit.framework.TestCase

class TempUISettingsTest : TestCase() {

    val defaultUISettings =
        UISettings()

    fun testToUISettings() {
        assertEquals(
            defaultUISettings.settings,
            when (val x = TempUISettings(defaultUISettings).toUISettings()) {
                is TempUISettings.ToUISettingsResult.Ok -> x.settings
                else -> null
            }
        )
    }

    fun testUpdateSetting() {
        val t = TempUISettings(defaultUISettings)
        assertEquals(t, t.updateSetting("bad key" to "ho"))
        assertEquals(
            t.copy(settings = t.settings.mapValues { _ -> "oi" }),
            t.settings.keys.fold(t, { nt, s -> nt.updateSetting(s to "oi") })
        )
    }
}