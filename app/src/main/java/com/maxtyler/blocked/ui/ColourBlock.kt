package com.maxtyler.blocked.ui

import androidx.compose.ui.graphics.Color
import com.maxtyler.blocked.game.Piece

object ColourBlock {
    fun rachelColour(piece: Piece): Color = when (piece) {
        Piece.I -> Color(177, 186, 140)
        Piece.J -> Color(200, 189, 242)
        Piece.L -> Color(161, 114, 128)
        Piece.S -> Color(86, 107, 101)
        Piece.O -> Color(87, 3, 28)
        Piece.Z -> Color(83, 194, 161)
        Piece.T -> Color(49, 4, 209)
    }

    fun natisColour(piece: Piece): Color = when (piece) {
        Piece.I -> Color(204, 65, 149)
        Piece.J -> Color(116, 116, 117)
        Piece.L -> Color(50, 82, 62)
        Piece.S -> Color(102, 22, 70)
        Piece.O -> Color(42, 50, 115)
        Piece.Z -> Color(196, 182, 51)
        Piece.T -> Color(134, 235, 174)
    }

    fun toColour(piece: Piece): Color = rachelColour(piece)
}