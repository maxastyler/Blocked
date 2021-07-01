package com.maxtyler.blocked.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Settings(
    @PrimaryKey val settingsId: Int = 0,
    val dragLimit: Float,
    val holdLimit: Float,
    val hardDropLimit: Float,
    val dropHorizontalMultiplier: Float,
    val hardDropVibrationTime: Long,
    val hardDropVibrationStrength: Int,
    val dropVibrationTime: Long,
    val dropVibrationStrength: Int,
    val coloursId: Int?,
)
