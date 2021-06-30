package com.maxtyler.blocked.game

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ColourSettings(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val backgroundColour: Color = Color(240, 240, 240),
    val shadowColour: Color = Color.LightGray,
    val IColour: Color = Color(177, 186, 140),
    val JColour: Color = Color(200, 189, 242),
    val TColour: Color = Color(161, 114, 128),
    val SColour: Color = Color(86, 107, 101),
    val ZColour: Color = Color(87, 3, 28),
    val LColour: Color = Color(83, 194, 161),
    val OColour: Color = Color(49, 4, 209),
) {
    override fun toString(): String = super.toString()

    fun pieceColour(piece: Piece) = when (piece) {
        Piece.I -> this.IColour
        Piece.J -> this.JColour
        Piece.L -> this.LColour
        Piece.O -> this.OColour
        Piece.T -> this.TColour
        Piece.Z -> this.ZColour
        Piece.S -> this.SColour
    }
}
