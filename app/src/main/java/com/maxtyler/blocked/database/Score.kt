package com.maxtyler.blocked.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Score(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val score: Int,
    val date: Date,
)