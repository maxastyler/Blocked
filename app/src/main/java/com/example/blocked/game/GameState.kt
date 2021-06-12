package com.example.blocked.game

data class GameState(
    val position: Vec2,
    val rotation: Rotation,
    val board: Board,
    val pieces: List<Piece>,
    val score: Int
)