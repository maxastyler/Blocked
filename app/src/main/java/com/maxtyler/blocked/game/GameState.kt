package com.maxtyler.blocked.game

import androidx.compose.ui.graphics.Color
import kotlin.math.pow

fun colourBlockfromString(s: String): ColourBlock = when (s) {
    "I" -> ColourBlock.I
    "J" -> ColourBlock.J
    "L" -> ColourBlock.L
    "S" -> ColourBlock.S
    "O" -> ColourBlock.O
    "Z" -> ColourBlock.Z
    else -> ColourBlock.T
}

enum class ColourBlock : Block {
    I, J, L, S, O, Z, T;

    override fun toString(): String = when (this) {
        I -> "I"
        J -> "J"
        L -> "L"
        S -> "S"
        O -> "O"
        Z -> "Z"
        T -> "T"
    }

    fun rachelColour(): Color = when (this) {
        I -> Color(177, 186, 140)
        J -> Color(200, 189, 242)
        L -> Color(161, 114, 128)
        S -> Color(86, 107, 101)
        O -> Color(87, 3, 28)
        Z -> Color(83, 194, 161)
        T -> Color(49, 4, 209)
    }

    fun natisColour(): Color = when (this) {
        I -> Color(204, 65, 149)
        J -> Color(116, 116, 117)
        L -> Color(50, 82, 62)
        S -> Color(102, 22, 70)
        O -> Color(42, 50, 115)
        Z -> Color(196, 182, 51)
        T -> Color(134, 235, 174)
    }

    override fun toColour(): Color = rachelColour()

    companion object {

        fun fromPiece(piece: Piece): ColourBlock = when (piece) {
            Piece.I -> I
            Piece.J -> J
            Piece.L -> L
            Piece.S -> S
            Piece.O -> O
            Piece.Z -> Z
            Piece.T -> T
        }
    }
}

data class GameState(
    val position: Vec2,
    val rotation: Rotation,
    val board: Board,
    val pieces: List<Piece>,
    val held: Piece?,
    val mode: Mode,
    val lockDelay: LockDelay,
    val settings: GameSettings,
    val score: Score = Score(),
    val holdUsed: Boolean = false,
) {

    data class Score(
        val score: Int = 0,
        val lastClearWasTetris: Boolean = false,
        val level: Int = 1,
        val levelStartScore: Int = 0,
    ) {
        fun checkLevelUp(): Score =
            if (score - levelStartScore >= 5 * level * level) {
                this.copy(
                    level = level + 1,
                    levelStartScore = score
                )
            } else this

    }

    data class GameSettings(
        val startingHeight: Int,
        val gameOverHeight: Int,
    ) {
        constructor(startingHeight: Int) : this(
            startingHeight = startingHeight,
            gameOverHeight = startingHeight
        )
    }

    data class LockDelay(
        val rotationsLeft: Int,
        val movesLeft: Int,
        val rotationLimit: Int,
        val moveLimit: Int,
        val timeOut: Long = 500L,
    ) {
        constructor(rotationLimit: Int, moveLimit: Int) : this(
            rotationsLeft = rotationLimit,
            movesLeft = moveLimit,
            rotationLimit = rotationLimit,
            moveLimit = moveLimit
        )

        fun reset(): LockDelay =
            this.copy(rotationsLeft = this.rotationLimit, movesLeft = this.moveLimit)
    }

    enum class Mode {
        Playing,
        Paused,
        GameOver,
    }

    constructor(width: Int, height: Int) : this(
        position = Vec2(width / 2, height),
        rotation = Rotation.None,
        board = Board(width = width, height = height, blocks = mapOf()),
        pieces = Piece.shuffled(),
        held = null,
        mode = Mode.Playing,
        lockDelay = LockDelay(15, 15),
        settings = GameSettings(22)
    )

    /**
     * To to put the piece in the new position
     */
    fun tryPosition(newPosition: Vec2): GameState? =
        if (board.isValidPosition(this.pieces.first(), newPosition, this.rotation)) {
            this.copy(position = newPosition)
        } else null

    /**
     * Try to rotate the piece to the new rotation
     */
    fun tryRotation(newRotation: Rotation): GameState? {
        val piece = this.pieces.first()
        piece.getKicks(this.rotation, newRotation).forEach { kick ->
            if (board.isValidPosition(piece, this.position + kick, newRotation)) {
                return this.copy(position = this.position + kick, rotation = newRotation)
            }
        }
        return null
    }

    /**
     * Get the dropped position of this piece
     */
    fun getDroppedPosition(): Vec2 {
        var dropPos = this.position
        var newDropPos = dropPos
        while (board.isValidPosition(this.pieces.first(), newDropPos, this.rotation)) {
            dropPos = newDropPos
            newDropPos += Vec2(0, -1)
        }
        return dropPos
    }

    fun getShadow(): List<Vec2> = this.pieces.first().getCoordinates(this.rotation)
        .map { coord -> getDroppedPosition() + coord }

    /**
     * Return a pair of (the new game state, whether the piece was locked)
     */
    fun drop(): Pair<GameState, Boolean> =
        when (val newState = tryPosition(this.position - Vec2(0, 1))) {
            is GameState -> Pair(newState.resetLockDelay(), false)
            else -> Pair(addPieceToBoard().resetLockDelay(), true)
        }

    fun hardDrop(): GameState {
        val pos = getDroppedPosition()
        when (val newState = tryPosition(pos)) {
            null -> return this.drop().first
            else -> return newState.drop().first
        }
    }

    /**
     * If there are less than 7 pieces in the game state, generate some more
     * @return The game state with more pieces
     */
    fun ensureEnoughPieces(): GameState = if (this.pieces.size < 7) {
        this.copy(pieces = this.pieces + Piece.shuffled())
    } else {
        this
    }

    fun addScore(clearedLines: Set<Int>): GameState = this.copy(
        score = this.score.copy(
            lastClearWasTetris = (clearedLines.size >= 4),
            score = this.score.score + when (clearedLines.size) {
                0 -> 0
                1 -> 1 * this.score.level
                2 -> 3 * this.score.level
                3 -> 5 * this.score.level
                else -> if (this.score.lastClearWasTetris) 12 * this.score.level else 8 * this.score.level
            }
        ).checkLevelUp()
    )

    fun addPieceToBoard(): GameState {
        var newBoard =
            board.addPiece(
                this.pieces.first(),
                this.position,
                this.rotation
            ) { ColourBlock.fromPiece(it) }
        val (clearedLines, lineOffsets) = newBoard.getLineOffsets()
        newBoard = newBoard.applyRowChanges(clearedLines, lineOffsets)
        val isPieceAboveLimit = (0 until newBoard.width).any {
            newBoard.blocks.containsKey(
                Vec2(
                    it,
                    this.settings.gameOverHeight
                )
            )
        }
        return this.copy(
            board = newBoard,
            pieces = this.pieces.drop(1),
            holdUsed = false,
            mode = if (isPieceAboveLimit) Mode.GameOver else this.mode
        ).addScore(clearedLines).getNextPiece()
    }

    fun getNextPiece(): GameState {
        val newState = this.ensureEnoughPieces().resetPosition()
        return newState.copy(
            mode = if (!newState.board.isValidPosition(
                    newState.pieces.first(),
                    newState.position,
                    newState.rotation
                )
            ) {
                Mode.GameOver
            } else {
                newState.mode
            }
        )
    }

    fun holdPiece(): GameState = if (!holdUsed) {
        when (held) {
            null -> this.copy(
                held = this.pieces.first(),
                pieces = this.pieces.drop(1)
            ).ensureEnoughPieces()
            else -> this.copy(
                held = this.pieces.first(),
                pieces = listOf(this.held) + this.pieces.drop(1)
            )
        }.copy(holdUsed = true).resetPosition()
    } else this

    fun dropTime(): Long =
        ((0.8 - ((this.score.level - 1) * 0.007)).pow(this.score.level - 1) * 1000).toLong()

    fun resetPosition(): GameState = this.copy(
        position = Vec2(
            board.width / 2 - pieces.first().offset, if (pieces.first() == Piece.I) {
                settings.startingHeight - 1
            } else settings.startingHeight
        ), rotation = Rotation.None
    )

    fun resetLockDelay(): GameState = this.copy(lockDelay = this.lockDelay.reset())
    fun useLockRotation(): GameState =
        this.copy(lockDelay = this.lockDelay.copy(rotationsLeft = this.lockDelay.rotationsLeft - 1))

    fun useLockMovement(): GameState =
        this.copy(lockDelay = this.lockDelay.copy(movesLeft = this.lockDelay.movesLeft - 1))

    fun pause(): GameState = this.copy(mode = Mode.Paused)
    fun resume(): GameState = this.copy(mode = Mode.Playing)

}

