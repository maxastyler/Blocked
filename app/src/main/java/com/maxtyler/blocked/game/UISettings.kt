package com.maxtyler.blocked.game

data class UISettings(
    val dragLimit: Float = 50F,
    val holdLimit: Float = 200F,
    val hardDropLimit: Float = 130F,
    val dropHorizontalMultiplier: Float = 3F,
)
