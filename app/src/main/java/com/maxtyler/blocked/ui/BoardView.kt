package com.example.blocked.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maxtyler.blocked.game.*

@Composable
fun BoardView(gameState: GameState, colourSettings: ColourSettings) {
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

        drawRect(colourSettings.backgroundColour, size = Size(innerWidth, innerHeight))

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
            drawBlock(Offset(xpos, ypos), blockSize, block.colour(colourSettings))
        }

        gameState.getShadow().forEach { pos ->
            val xpos = blockLength * pos.x
            val ypos = innerHeight - blockLength * pos.y
            drawBlock(Offset(xpos, ypos), blockSize, colourSettings.shadowColour)
        }

        gameState.pieceState.coordinates.forEach { pos ->
            if (pos.y < renderHeight) {
                val xpos = blockLength * pos.x
                val ypos = innerHeight - blockLength * pos.y
                drawBlock(
                    Offset(xpos, ypos),
                    blockSize,
                    gameState.pieceState.piece.colour(colourSettings)
                )
            }
        }
    }
}

fun DrawScope.drawBlock(offset: Offset, size: Size, color: Color) {
    drawRect(topLeft = offset, size = size, color = color)
}

@Composable
fun DrawPiece(piece: Piece, colourSettings: ColourSettings) {
    val coords = piece.getCoordinates(Rotation.None)
    val minX = coords.minOfOrNull { it.x }!!
    val minY = coords.minOfOrNull { it.y }!!
    Canvas(modifier = Modifier.fillMaxSize()) {
        val blockX = size.width / 4
        val blockY = size.height / 4
        drawRect(Color.White)
        (0 until 4).forEach { x ->
            (0 until 4).forEach { y ->
                if (coords.contains(Vec2(x + minX, y + minY))) {
                    val offset = Offset(x * blockX, (3 - y) * blockY)
                    drawRect(
                        color = piece.colour(colourSettings),
                        topLeft = offset,
                        size = Size(blockX, -blockY)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun DrawPreview() {
    Column {
        listOf(Piece.T, Piece.Z, Piece.O, Piece.S, Piece.L, Piece.I, Piece.J).forEach {
            Spacer(modifier = Modifier.height(2.dp))
            Box(modifier = Modifier.size(10.dp)) {
                DrawPiece(piece = it, ColourSettings())
            }
        }
    }

}