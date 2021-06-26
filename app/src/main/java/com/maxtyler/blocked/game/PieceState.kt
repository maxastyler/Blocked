package com.maxtyler.blocked.game

/**
 * The state of the current playing piece
 * @param piece The piece (I, J, L, S, Z, O, T)
 * @param position The central position of the piece
 * @param rotation The rotation of the piece
 */
data class PieceState(val piece: Piece, val position: Vec2, val rotation: Rotation) {

    /**
     * All of the coordinates in the piece
     */
    val coordinates: List<Vec2>
        get() = piece.getCoordinates(rotation).map { it + position }
}
