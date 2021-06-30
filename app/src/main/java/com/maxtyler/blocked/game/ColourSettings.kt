package com.maxtyler.blocked.game

import androidx.compose.ui.graphics.Color

data class ColourSettings(
    val backgroundColour: Color,
    val IColour: Color,
    val JColour: Color,
    val TColour: Color,
    val SColour: Color,
    val ZColour: Color,
    val LColour: Color,
    val OColour: Color,
) {
    override fun toString(): String {
        return super.toString()
    }
}
