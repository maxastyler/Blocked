package com.example.blocked.game

interface Block {

}

data class Board(val width: Int, val height: Int, val blocks: Map<Vec2, Block>) {
    fun isValidPosition(piece: Piece, position: Vec2, rotation: Rotation): Boolean {
        piece.getCoordinates(rotation)
            .map { coord -> coord + position }
            .forEach { pos ->
                if ((pos.x < 0) ||
                    (pos.x >= this.width) ||
                    (pos.y < 0) ||
                    (pos.y >= this.height) ||
                    blocks.containsKey(pos)
                ) return false
            }
        return true
    }

    /**
     * Get the offsets to move a line down with
     * @return A pair of the indices that need to be delete, and the rows mapped to their drop distance
     */
    fun getLineOffsets(): Pair<Set<Int>, Map<Int, Int>> {
        val fullRows = (0 until this.height).map { i ->
            (0 until this.width).all { j -> this.blocks.containsKey(Vec2(i, j)) }
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

    fun applyRowChanges(toDelete: Set<Int>, toDrop: Map<Int, Int>): Board {
        return this.copy(blocks = this.blocks.filter { (k, _) -> !(k.y in toDelete) }
            .mapKeys { Vec2(it.key.x, toDrop.getOrDefault(it.key.y, 0)) })
    }

    companion object {

    }
}