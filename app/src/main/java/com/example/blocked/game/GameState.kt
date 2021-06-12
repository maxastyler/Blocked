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
    val score: Int
) {

    constructor(width: Int, height: Int) : this(
        position = Vec2(width / 2, height),
        rotation = Rotation.None,
        board = Board(width = width, height = height, blocks = mapOf()),
        pieces = Piece.shuffled(),
        score = 0
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

    fun drop(): GameState = when (val newState = tryPosition(this.position - Vec2(0, 1)).apply { println(this) }) {
        is GameState -> newState
        else -> addPieceToBoard()
    }

    fun hardDrop(): GameState {
        val pos = getDroppedPosition()
        when (val newState = tryPosition(pos)) {
            null -> throw IllegalStateException("Couldn't hard drop!")
            else -> return newState.drop()
        }
    }

    fun addPieceToBoard(): GameState {
        val newBoard =
            board.addPiece(this.pieces.first(), this.position, this.rotation, { MyBlock })
        val (clearedLines, lineOffsets) = newBoard.getLineOffsets()
        return this.copy(
            board = newBoard.applyRowChanges(clearedLines, lineOffsets),
            score = this.score + clearedLines.size,
            position = Vec2(this.board.width / 2, this.board.height),
            rotation = Rotation.None,
            pieces = if (pieces.size < 7) this.pieces.drop(1) + pieces.shuffled() else this.pieces.drop(
                1
            )
        )
    }
}

