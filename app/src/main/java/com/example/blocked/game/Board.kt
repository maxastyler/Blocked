package com.example.blocked.game

import androidx.compose.ui.graphics.Color

interface Block {
    fun toColour(): Color
}

data class Board(val width: Int, val height: Int, val blocks: Map<Vec2, Block>) {

    /**
     * Check if the piece with the given position and rotation is in a valid position
     * @param piece The piece to fit
     * @param position The base position of the piece
     * @param rotation The rotation of the piece
     * @return Whether the piece is in bounds and non-overlapping
     */
    fun isValidPosition(piece: Piece, position: Vec2, rotation: Rotation): Boolean {
        piece.getCoordinates(rotation)
            .map { coord -> coord + position }
            .forEach { pos ->
                if ((pos.x < 0) ||
                    (pos.x >= this.width) ||
                    (pos.y < 0) ||
                    blocks.containsKey(pos)
                ) return false
            }
        return true
    }

    fun addPiece(
        piece: Piece,
        position: Vec2,
        rotation: Rotation,
        blockFun: (Piece) -> Block
    ): Board = this.copy(blocks = this.blocks + (piece.getCoordinates(rotation)
            .map { coord -> (coord + position) to blockFun(piece) }
            .toMap()))

    /**
     * Get the offsets to move a line down with
     * @return A pair of the indices that need to be delete, and the rows mapped to their drop distance
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

    companion object {

    }
}