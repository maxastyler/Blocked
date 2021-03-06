package com.maxtyler.blocked.game

data class GameState(
    val pieceState: PieceState,
    val board: Board,
    val pieces: List<Piece>,
    val held: Piece?,
    val mode: Mode,
    val lockDelay: LockState,
    val settings: GameSettings,
    val score: Score = Score(),
    val holdUsed: Boolean = false,
) {

    companion object {
        fun fromWidthAndHeight(width: Int, height: Int): GameState {
            val pieces = Piece.shuffled()
            return GameState(
                pieceState = PieceState(pieces.first(), Vec2(0, 0), Rotation.None),
                board = Board(width = width, height = height, blocks = mapOf()),
                pieces = pieces.drop(1),
                held = null,
                mode = Mode.Playing,
                lockDelay = LockState(15, 15),
                settings = GameSettings(22)
            ).ensureEnoughPieces()
        }
    }

    enum class Mode {
        Playing,
        Paused,
        GameOver,
    }

    /**
     * To to put the piece in the new position
     */
    fun tryPosition(newPosition: Vec2): GameState? {
        val newPieceState: PieceState = this.pieceState.copy(position = newPosition)
        return if (board.isValidPosition(newPieceState)) {
            this.copy(pieceState = newPieceState)
        } else null
    }

    /**
     * Try to rotate the piece to the new rotation
     */
    fun tryRotation(newRotation: Rotation): GameState? {
        pieceState.piece.getKicks(pieceState.rotation, newRotation).forEach { kick ->
            val newPieceState =
                pieceState.copy(position = pieceState.position + kick, rotation = newRotation)
            if (board.isValidPosition(newPieceState)) {
                return this.copy(pieceState = newPieceState)
            }
        }
        return null
    }

    /**
     * Get the dropped position of this piece
     */
    fun getDroppedPosition(): Vec2 {
        var dropPos = pieceState.position
        var newDropPos = dropPos
        while (board.isValidPosition(pieceState.copy(position = newDropPos))) {
            dropPos = newDropPos
            newDropPos += Vec2(0, -1)
        }
        return dropPos
    }

    fun getShadow(): List<Vec2> = pieceState.copy(position = getDroppedPosition()).coordinates

    sealed class DropReturn
    data class Dropped(val gameState: GameState) : DropReturn()

    /**
     * Drop and return the new state and whether the piece was locked or not
     */
    fun drop(): DropReturn =
        when (val newState = tryPosition(pieceState.position - Vec2(0, 1))) {
            is GameState -> Dropped(newState.resetLockDelay())
            else -> addPieceToBoard().let { it.copy(gameState = it.gameState.resetLockDelay()) }
        }

    fun hardDrop(): AddPieceToBoardReturn {
        val pos = getDroppedPosition()
        return when (val newState = tryPosition(pos)) {
            null -> this.drop() as AddPieceToBoardReturn
            else -> newState.drop() as AddPieceToBoardReturn
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

    /**
     * Add to the state's score from the cleared lines
     * @param clearedLines A set of the cleared line indices
     * @return A pair of (new state, did level up?)
     */
    fun addScore(clearedLines: Set<Int>): Pair<GameState, Boolean> {
        val (newScore, levelledUp) = this.score.copy(
            lastClearWasTetris = (clearedLines.size >= 4),
            score = this.score.score + when (clearedLines.size) {
                0 -> 0
                1 -> 1 * this.score.level
                2 -> 3 * this.score.level
                3 -> 5 * this.score.level
                else -> if (this.score.lastClearWasTetris) 12 * this.score.level else 8 * this.score.level
            }
        ).checkLevelUp()
        return Pair(this.copy(score = newScore), levelledUp)
    }


    data class AddPieceToBoardReturn(
        val gameState: GameState,
        val gameOver: Boolean,
        val levelledUp: Boolean
    ) : DropReturn()

    /**
     * Add the current piece to the board, update the score and check whether it's game over
     * @return A pair of (the new game state with updated piece and score, if the game is over)
     */
    fun addPieceToBoard(): AddPieceToBoardReturn {

        var newBoard =
            board.addPiece(pieceState)
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
        val (newState, levelledUp) = this.copy(board = newBoard).getNextPiece()
            .addScore(clearedLines)

        val gameOver = isPieceAboveLimit || (!newState.board.isValidPosition(newState.pieceState))

        return AddPieceToBoardReturn(
            newState.copy(mode = if (gameOver) Mode.GameOver else newState.mode),
            gameOver = gameOver,
            levelledUp = levelledUp
        )
    }

    /**
     * Get the next piece from the queue
     * @return the game state with the new piece added. You should check if this new piece
     * causes a game over once added.
     */
    fun getNextPiece(): GameState = this.ensureEnoughPieces().let {
        it.copy(
            pieceState = it.pieceState.copy(piece = it.pieces.first()),
            pieces = it.pieces.drop(1),
            holdUsed = false,
        )
    }.resetPosition()

    /**
     * Hold a piece in the hold spot
     * @return A pair of (the new game state, whether the piece was held)
     */
    fun holdPiece(): Pair<GameState, Boolean> = if (!holdUsed) {
        Pair(when (held) {
            null -> this.ensureEnoughPieces().let {
                it.copy(
                    held = it.pieceState.piece,
                    pieceState = it.pieceState.copy(piece = it.pieces.first()),
                    pieces = it.pieces.drop(1)
                )
            }
            else -> this.copy(
                held = this.pieceState.piece,
                pieceState = this.pieceState.copy(piece = this.held)
            )
        }.copy(holdUsed = true).resetPosition(), true)
    } else Pair(this, false)

    /**
     * Reset the position of the piece to the top of the game board
     * @return The game state with the piece reset
     */
    fun resetPosition(): GameState = this.copy(
        pieceState = pieceState.copy(
            position = Vec2(
                board.width / 2 - pieceState.piece.offset, if (pieceState.piece == Piece.I) {
                    settings.startingHeight - 1
                } else settings.startingHeight
            ), rotation = Rotation.None
        )
    )

    fun resetLockDelay(): GameState = this.copy(lockDelay = this.lockDelay.reset())

    fun useLockRotation(): GameState =
        this.copy(lockDelay = this.lockDelay.copy(rotationsLeft = this.lockDelay.rotationsLeft - 1))

    fun useLockMovement(): GameState =
        this.copy(lockDelay = this.lockDelay.copy(movesLeft = this.lockDelay.movesLeft - 1))

    fun pause(): GameState = if (this.mode == Mode.GameOver) this else this.copy(mode = Mode.Paused)
    fun resume(): GameState =
        if (this.mode == Mode.GameOver) this else this.copy(mode = Mode.Playing)
}

