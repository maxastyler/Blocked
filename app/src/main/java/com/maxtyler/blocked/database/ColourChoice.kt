package com.maxtyler.blocked.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ColourChoice(
    @PrimaryKey val id: Int = 0,
    val colourSettingsId: Int?
)
