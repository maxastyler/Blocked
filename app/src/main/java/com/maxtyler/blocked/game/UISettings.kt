package com.maxtyler.blocked.game

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UISettings(
    @PrimaryKey val id: Int = 0,
    val dragLimit: Float = 50F,
    val holdLimit: Float = 200F,
    val hardDropLimit: Float = 130F,
    val dropHorizontalMultiplier: Float = 3F,
    @Embedded val colourSettings: ColourSettings = ColourSettings(),
)
