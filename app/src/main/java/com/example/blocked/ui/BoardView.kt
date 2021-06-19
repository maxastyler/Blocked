package com.example.blocked.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BoardView(gameState: GameState) {
    val board = gameState.board
    Canvas(modifier = Modifier.fillMaxSize()) {

        // calculate the x width of a block
        val renderHeight = 20
        val rectX = size.width / board.width
        val rectY = size.height / renderHeight
        val blockLength = minOf(rectX, rectY)
        val blockSize = Size(blockLength, -blockLength)
        val innerWidth = blockLength * board.width
        val innerHeight = blockLength * renderHeight

        drawRect(Color(240, 240, 240), size = Size(innerWidth, innerHeight))

        (0..board.width).forEach {
            val p = it.toFloat() * blockLength
            drawLine(start = Offset(p, 0F), end = Offset(p, innerHeight), color = Color.Black)
        }
        (0..renderHeight).forEach {
            val p = it.toFloat() * blockLength
            drawLine(start = Offset(0F, p), end = Offset(innerWidth, p), color = Color.Black)
        }
        board.blocks.forEach { (pos, block) ->
            val xpos = blockLength * pos.x
            val ypos = innerHeight - blockLength * pos.y
            drawBlock(Offset(xpos, ypos), blockSize, block.toColour())
        }

        gameState.getShadow().forEach { pos ->
            val xpos = blockLength * pos.x
            val ypos = innerHeight - blockLength * pos.y
            drawBlock(Offset(xpos, ypos), blockSize, Color.LightGray)
        }

        gameState.pieces.first().getCoordinates(gameState.rotation).forEach { nPos ->
            val pos = nPos + gameState.position
            if (pos.y < renderHeight) {
                val xpos = blockLength * pos.x
                val ypos = innerHeight - blockLength * pos.y
                drawBlock(
                    Offset(xpos, ypos),
                    blockSize,
                    ColourBlock.fromPiece(gameState.pieces.first()).toColour()
                )
            }
        }
    }
}

fun DrawScope.drawBlock(offset: Offset, size: Size, color: Color) {
    drawRect(topLeft = offset, size = size, color = color)
}

@Composable
fun DrawPiece(piece: Piece) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(Color.White)
    }
}

@Preview
@Composable
fun DrawPreview() {
    Column() {
        listOf(Piece.T, Piece.Z, Piece.O, Piece.S, Piece.L, Piece.I, Piece.J).forEach {
            Box(modifier = Modifier.size(10.dp)) {
                DrawPiece(Piece.T)
            }
        }
    }

}