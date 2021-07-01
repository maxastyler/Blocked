package com.maxtyler.blocked.database

import androidx.room.Embedded
import androidx.room.Relation

data class SettingsAndColours(

    @Embedded val colourChoice: ColourChoice,
    @Relation(parentColumn = "colourSettingsId", entityColumn = "coloursId")
    val colours: Colours?,
    @Relation(parentColumn = "id", entityColumn = "parentKey")
    val settings: List<Setting>,
)
