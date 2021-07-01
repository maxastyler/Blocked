package com.maxtyler.blocked.game

data class UISettings(
    val dragLimit: Float = 50F,
    val holdLimit: Float = 200F,
    val hardDropLimit: Float = 130F,
    val dropHorizontalMultiplier: Float = 3F,
    val dropVibrationTime: Long = 10L,
    val dropVibrationStrength: Int = 30,
    val hardDropVibrationTime: Long = 100L,
    val hardDropVibrationStrength: Int = 200,
    val colourSettings: Pair<Int?, ColourSettings> = Pair(
        null, ColourSettings()
    ),
)
