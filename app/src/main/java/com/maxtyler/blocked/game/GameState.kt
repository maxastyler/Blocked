package com.maxtyler.blocked.game

import androidx.compose.ui.graphics.Color

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

    constructor(width: Int, height: Int) : this(
        pieceState = PieceState(Piece.I, Vec2(0, 0), Rotation.None),
        board = Board(width = width, height = height, blocks = mapOf()),
        pieces = Piece.shuffled(),
        held = null,
        mode = Mode.Playing,
        lockDelay = LockState(15, 15),
        settings = GameSettings(22)
    ) {
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
     * Return a pair of (the new game state, whether the piece was locked)
     */
    fun drop(): DropReturn =
        when (val newState = tryPosition(pieceState.position - Vec2(0, 1))) {
            is GameState -> Dropped(newState.resetLockDelay())
            else -> addPieceToBoard().let { it.copy(gameState = it.gameState.resetLockDelay()) }
        }

    fun hardDrop(): DropReturn {
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

    fun pause(): GameState = this.copy(mode = Mode.Paused)
    fun resume(): GameState = this.copy(mode = Mode.Playing)
}

