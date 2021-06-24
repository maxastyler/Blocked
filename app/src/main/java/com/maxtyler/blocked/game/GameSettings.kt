package com.maxtyler.blocked.game

/**
 * This class contains settings for the game
 */
data class GameSettings(
    val startingHeight: Int,
    val gameOverHeight: Int
) {
    constructor(startingHeight: Int) : this(
        startingHeight = startingHeight,
        gameOverHeight = startingHeight
    )
}
