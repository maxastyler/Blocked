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
    val score: Int,
    val held: Piece?,
    val mode: Mode,
    val holdUsed: Boolean = false,
) {

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
        score = 0,
        held = null,
        mode = Mode.Playing,
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

    fun drop(): GameState =
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

    fun addPieceToBoard(): GameState {
        var newBoard =
            board.addPiece(this.pieces.first(), this.position, this.rotation) { MyBlock }
        val (clearedLines, lineOffsets) = newBoard.getLineOffsets()
        val newPosition = Vec2(this.board.width / 2, this.board.height)
        newBoard = newBoard.applyRowChanges(clearedLines, lineOffsets)
        return this.copy(
            board = newBoard,
            score = this.score + clearedLines.size,
            position = newPosition,
            rotation = Rotation.None,
            pieces = this.pieces.drop(1),
            mode = if (!newBoard.isValidPosition(this.pieces[1], newPosition, Rotation.None)) {
                Mode.GameOver
            } else {
                this.mode
            },
            holdUsed = false,
        ).ensureEnoughPieces()
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
        }.copy(
            position = Vec2(board.width / 2, board.height),
            rotation = Rotation.None,
            holdUsed = true
        )
    } else this

    fun pause(): GameState = this.copy(mode = Mode.Paused)
    fun resume(): GameState = this.copy(mode = Mode.Playing)
}

