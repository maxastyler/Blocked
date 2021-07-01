package com.maxtyler.blocked.game

data class UISettings(
    val settings: Map<String, SettingType<out Number>> = defaultSettings,
    val colourSettings: Pair<Int?, ColourSettings> = Pair(
        null, ColourSettings()
    ),
) {
    companion object {
        fun fromValuesAndDefaults(
            values: Map<String, SettingType<out Number>>,
            colourSettings: Pair<Int?, ColourSettings> = Pair(
                null,
                ColourSettings()
            )
        ) = UISettings(defaultSettings + values, colourSettings)

        val defaults: Map<String, Triple<Any, Any?, Any?>> = mapOf(
            "dragLimit" to Triple(50F, null, null),
            "holdLimit" to Triple(200F, null, null),
            "hardDropLimit" to Triple(130F, null, null),
            "dropHorizontalMultiplier" to Triple(3F, null, null),
            "dropVibrationTime" to Triple(10L, null, null),
            "dropVibrationStrength" to Triple(30, 0, 255),
            "hardDropVibrationTime" to Triple(100L, null, null),
            "hardDropVibrationStrength" to Triple(200, 0, 255),
        )

        val defaultSettings: Map<String, SettingType<out Number>> = defaults.map { x ->
            x.key to (when (x.value.first) {
                is Float -> SettingType.FloatSetting(
                    x.key,
                    default = x.value.first as Float,
                    x.value.first as Float,
                    x.value.second as Float?,
                    x.value.third as Float?
                )
                is Int -> SettingType.IntSetting(
                    x.key,
                    default = x.value.first as Int,
                    x.value.first as Int,
                    x.value.second as Int?,
                    x.value.third as Int?
                )
                is Long -> SettingType.LongSetting(
                    x.key,
                    default = x.value.first as Long,
                    x.value.first as Long,
                    x.value.second as Long?,
                    x.value.third as Long?
                )
                else -> throw IllegalStateException("Couldn't convert setting")
            })
        }.toMap()
    }
}
