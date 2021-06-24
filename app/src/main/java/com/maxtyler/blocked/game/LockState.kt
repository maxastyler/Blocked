package com.maxtyler.blocked.game

/**
 * The state describing piece locking
 */
data class LockState(
    val rotationsLeft: Int,
    val movesLeft: Int,
    val rotationLimit: Int,
    val moveLimit: Int,
    val lowestHeight: Int?,
    val timeOut: Long = 500L
) {
    /**
     * Create a lock state from move and rotation limits
     */
    constructor(rotationLimit: Int, moveLimit: Int) : this(
        rotationsLeft = rotationLimit,
        movesLeft = moveLimit,
        moveLimit = moveLimit,
        rotationLimit = rotationLimit,
        lowestHeight = null,
    )

    /**
     * Reset the rotationsLeft and movesLeft variables to their limits and the lowestHeight to null
     * @return The reset lock state
     */
    fun reset(): LockState =
        this.copy(rotationsLeft = rotationLimit, movesLeft = moveLimit, lowestHeight = null)
}
