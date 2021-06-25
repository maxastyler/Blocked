package com.maxtyler.blocked.game

import kotlin.math.pow

data class Score(
    val score: Int,
    val lastClearWasTetris: Boolean,
    val level: Int,
    val levelStartScore: Int
) {
    constructor() : this(score = 0, lastClearWasTetris = false, level = 1, levelStartScore = 0)

    /**
     * Check if we've got enough score to level up
     * @return A pair of (new score struct, did level up?)
     */
    fun checkLevelUp(): Pair<Score, Boolean> = if (score - levelStartScore >= 5 * level * level) {
        Pair(this.copy(level = level + 1, levelStartScore = score), true)
    } else Pair(this, false)

    /**
     * The current drop time
     */
    val dropTime: Long
        get() = ((0.8 - ((level - 1) * 0.007)).pow(level - 1) * 1000).toLong()

}
