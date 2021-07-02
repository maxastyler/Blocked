package com.maxtyler.blocked.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Colours(
    @PrimaryKey(autoGenerate = true) val coloursId: Int,
    var backgroundColour: Int,
    var shadowColour: Int,
    var IColour: Int,
    var JColour: Int,
    var TColour: Int,
    var SColour: Int,
    var ZColour: Int,
    var LColour: Int,
    var OColour: Int,
)
