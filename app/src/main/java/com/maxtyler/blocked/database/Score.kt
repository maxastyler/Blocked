package com.maxtyler.blocked.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Score(
    val score: Int,
    @PrimaryKey val date: Date,
)
