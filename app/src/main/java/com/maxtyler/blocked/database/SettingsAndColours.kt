package com.maxtyler.blocked.database

import androidx.room.Embedded
import androidx.room.Relation

data class SettingsAndColours(
    @Embedded val settings: Settings,
    @Relation(parentColumn = "coloursId", entityColumn = "coloursId")
    val colours: Colours?
)
