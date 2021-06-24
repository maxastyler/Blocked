package com.maxtyler.blocked.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maxtyler.blocked.game.Piece
import com.maxtyler.blocked.game.Rotation
import com.maxtyler.blocked.game.Vec2

@Entity
data class Save(
    @PrimaryKey val id: Int = 0,
    val width: Int,
    val height: Int,
    val board: Map<Vec2, Piece>,
    val score: Int,
    val lastClearWasTetris: Boolean = false,
    val level: Int = 1,
    val levelStartScore: Int = 0,
    val position: Vec2,
    val rotation: Rotation,
    val pieces: List<Piece>,
    val held: Piece?,
    val holdUsed: Boolean,
)