package com.maxtyler.blocked.database

import androidx.room.Embedded
import androidx.room.Relation

data class ChosenColours(
    @Embedded val colourChoice: ColourChoice,
    @Relation(
        parentColumn = "colourSettingsId",
        entityColumn = "coloursId",
    )
    val colours: Colours?,
)
