package com.example.blocked.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Score(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val score: Int
)
