package com.example.blocked.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun BoardView(gameState: GameState) {
    val board = gameState.board
    Canvas(modifier = Modifier.fillMaxSize()) {
        // calculate the x width of a block
        val rectX = size.width / board.width
        val rectY = size.height / board.height
        val blockLength = minOf(rectX, rectY)
        val blockSize = Size(blockLength, -blockLength)
        val innerWidth = blockLength * board.width
        val innerHeight = blockLength * board.height

        (0..board.width).forEach {
            val p = it.toFloat() * blockLength
            drawLine(start = Offset(p, 0F), end = Offset(p, innerHeight), color = Color.Black)
        }
        (0..board.height).forEach {
            val p = it.toFloat() * blockLength
            drawLine(start = Offset(0F, p), end = Offset(innerWidth, p), color = Color.Black)
        }
        board.blocks.forEach { (pos, _) ->
            val xpos = blockLength * pos.x
            val ypos = innerHeight - blockLength * pos.y
            drawRect(
                topLeft = Offset(xpos, ypos),
                size = blockSize,
                color = Color.Red
            )
        }
        gameState.getShadow().forEach { pos ->
            val xpos = blockLength * pos.x
            val ypos = innerHeight - blockLength * pos.y
            drawRect(
                topLeft = Offset(xpos, ypos),
                size = blockSize,
                color = Color.LightGray
            )
        }

        gameState.pieces.first().getCoordinates(gameState.rotation).forEach { nPos ->
            val pos = nPos + gameState.position
            val xpos = blockLength * pos.x
            val ypos = innerHeight - blockLength * pos.y
            drawRect(
                topLeft = Offset(xpos, ypos),
                size = blockSize,
                color = Color.Blue
            )
        }

    }
}
