package com.maxtyler.blocked.game

/**
 * The state of the board
 * @param width The width of the board
 * @param height The height of the board
 * @param blocks The blocks, a map of the position of the block to the piece type
 */
data class Board(val width: Int, val height: Int, val blocks: Map<Vec2, Piece>) {

    /**
     * Check if the piece with the given position and rotation is in a valid position
     * @param pieceState The piece state to fit
     * @return Whether the piece is in bounds and non-overlapping
     */
    fun isValidPosition(pieceState: PieceState): Boolean {
        pieceState.coordinates.forEach { pos ->
            if ((pos.x < 0) ||
                (pos.x >= this.width) ||
                (pos.y < 0) ||
                blocks.containsKey(pos)
            ) return false
        }
        return true
    }

    /**
     * Add the piece described by pieceState to the board
     * @param pieceState The piece
     * @return The new board with the piece added to it
     */
    fun addPiece(pieceState: PieceState): Board =
        this.copy(blocks = this.blocks + pieceState.coordinates.map { it -> it to pieceState.piece }
            .toMap())

    /**
     * Get the offsets to move a line down with
     * @return A pair of the indices that need to be deleted, and the rows mapped to their drop distance
     */
    fun getLineOffsets(): Pair<Set<Int>, Map<Int, Int>> {
        val fullRows = (0 until this.height).map { i ->
            (0 until this.width).all { j -> this.blocks.containsKey(Vec2(j, i)) }
        }

        val (_, toDelete, toDrop) = fullRows.foldIndexed(
            Triple(
                0,
                setOf<Int>(),
                mapOf<Int, Int>()
            )
        ) { i, (dropDistance, toDelete, toDrop), full ->
            if (full) {
                Triple(dropDistance + 1, toDelete + i, toDrop)
            } else {
                Triple(
                    dropDistance,
                    toDelete,
                    if (dropDistance > 0) (toDrop + (i to (i - dropDistance))) else toDrop
                )
            }
        }
        return Pair(toDelete, toDrop)
    }

    /**
     * Create a new board with the given row changes
     * @param toDelete The set of indices to delete
     * @param toDrop The set of indices to drop down
     * @return A new board with the changes applied
     */
    fun applyRowChanges(toDelete: Set<Int>, toDrop: Map<Int, Int>): Board {
        return this.copy(blocks = this.blocks.filter { (k, _) -> !(k.y in toDelete) }
            .mapKeys { Vec2(it.key.x, toDrop.getOrDefault(it.key.y, it.key.y)) })
    }

    /**
     * Chech if there are any pieces on the board at the given height
     * @param height The height to check against
     * @return Whether there was a piece at the given height
     */
    fun isAnyPieceAtHeight(height: Int): Boolean = (0 until width).any {
        blocks.containsKey(Vec2(it, height))
    }
}