package com.example.blocked.game

object MyBlock : Block {
    override fun toString(): String {
        return "B"
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
        val gameOverHeight: Int
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
        val moveLimit: Int
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
        settings = GameSettings(height)
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

    fun drop(computerDrop: Boolean = false): GameState =
        when (val newState = tryPosition(this.position - Vec2(0, 1))) {
            is GameState -> newState
            else -> addPieceToBoard()
        }

    fun hardDrop(): GameState {
        val pos = getDroppedPosition()
        when (val newState = tryPosition(pos)) {
            null -> return this.drop()
            else -> return newState.drop()
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
        score = Score(
            lastClearWasTetris = (clearedLines.size >= 4),
            score = this.score.score + when (clearedLines.size) {
                1 -> 1 * this.score.level
                2 -> 3 * this.score.level
                3 -> 5 * this.score.level
                else -> if (this.score.lastClearWasTetris) 12 * this.score.level else 8 * this.score.level
            }
        ).checkLevelUp()
    )

    fun addPieceToBoard(): GameState {
        var newBoard =
            board.addPiece(this.pieces.first(), this.position, this.rotation) { MyBlock }
        val (clearedLines, lineOffsets) = newBoard.getLineOffsets()
        newBoard = newBoard.applyRowChanges(clearedLines, lineOffsets)
        return this.copy(
            board = newBoard,
            pieces = this.pieces.drop(1),
            holdUsed = false,
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

    fun resetPosition(): GameState = this.copy(
        position = Vec2(
            board.width / 2 - pieces.first().offset, if (pieces.first() == Piece.I) {
                settings.startingHeight - 1
            } else settings.startingHeight
        ), rotation = Rotation.None
    )

    fun resetLockDelay(): GameState = this.copy(lockDelay = this.lockDelay.reset())

    fun pause(): GameState = this.copy(mode = Mode.Paused)
    fun resume(): GameState = this.copy(mode = Mode.Playing)
}

