package lewis.libby.hw4.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lewis.libby.hw4.*
import lewis.libby.hw4.R
import lewis.libby.hw4.components.detectTapDragGestures
import kotlin.math.min

@Composable
fun Gems(
    score: Int,
    shapes: List<Shape>,
    drag: Shape,
    updateGems: (List<Shape>) -> Unit,
    handlers: Handlers,
    highlightShapeType: ShapeType?,
    setOffset: (Int, Int, Float) -> Unit,
) {
    with(LocalDensity.current) {

        // Screen touch location variable
        var finger by remember { mutableStateOf(Offset.Zero) }

        // Draw diamond shape
        fun DrawScope.drawDiamond(outlineColor: Color, shapeSize: Float, boxSize: Float, outline: Stroke) {
            val shapeOffset = (boxSize - shapeSize)/2
            val diamondPath = Path().apply {
                moveTo(boxSize/2, shapeOffset)
                lineTo(shapeSize+shapeOffset, boxSize/2)
                lineTo(boxSize/2, shapeSize+shapeOffset)
                lineTo(shapeOffset, boxSize/2)
                close()
            }
            drawPath(path = diamondPath, color = Color.Red)
            drawPath(path = diamondPath, color = outlineColor, style = outline)
        }

        // Draw cross shape
        fun DrawScope.drawCross(outlineColor: Color, shapeSize: Float, boxSize: Float, outline: Stroke) {
            val shapeOffset = (boxSize - shapeSize)/2
            val crossPath = Path().apply {
                moveTo(shapeOffset+shapeSize/3, shapeOffset)
                lineTo(shapeOffset+shapeSize*2/3, shapeOffset)
                lineTo(shapeOffset+shapeSize*2/3, shapeOffset+shapeSize/3)
                lineTo(shapeOffset+shapeSize, shapeOffset+shapeSize/3)
                lineTo(shapeOffset+shapeSize, shapeOffset+shapeSize*2/3)
                lineTo(shapeOffset+shapeSize*2/3, shapeOffset+shapeSize*2/3)
                lineTo(shapeOffset+shapeSize*2/3, shapeOffset+shapeSize)
                lineTo(shapeOffset+shapeSize/3, shapeOffset+shapeSize)
                lineTo(shapeOffset+shapeSize/3, shapeOffset+shapeSize*2/3)
                lineTo(shapeOffset, shapeOffset+shapeSize*2/3)
                lineTo(shapeOffset, shapeOffset+shapeSize/3)
                lineTo(shapeOffset+shapeSize/3, shapeOffset+shapeSize/3)
                close()
            }
            drawPath(path = crossPath, color = Color.Green)
            drawPath(path = crossPath, color = outlineColor, style = outline)
        }

        // Scaffold for game screen
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.title)) },
                )
            },
            content = { padding ->
                Column(
                    modifier = Modifier
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 50.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${stringResource(R.string.score)} $score",
                            fontSize = 65.sp,
                        )
                    }
                    Row(
                        modifier = Modifier
                    ) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(handlers) {
                                    detectTapDragGestures(
                                        onTap = {
                                            finger = it
                                            val boxSize = min(size.width, size.height) /10f
                                            if ((it.x < boxSize) || (it.x > 9*boxSize)) {
                                                handlers.onPause()
                                            } else if ((it.y > boxSize*8)){
                                                handlers.onPause()
                                            } else {
                                                handlers.onHighLightShape(
                                                    finger, boxSize
                                                )
                                            }
                                        },
                                        onDragStart = { offset ->
                                            handlers.onDragStart(offset, min(size.width, size.height) /10f)
                                        },
                                        onDrag = { change, _ ->
                                            finger = change.position
                                            handlers.onDrag(change.position)
                                        },
                                        onDragEnd = {
                                            handlers.onDragEnd(finger, min(size.width, size.height) /10f)
                                        },
                                        onDragCancel = {
                                            handlers.onDragEnd(finger, min(size.width, size.height) /10f)
                                        },
                                    )
                                }
                        ) {
                            val minDim = min(size.width, size.height)
                            val boxSize = minDim/10f
                            val shapeSize = boxSize * 2/3
                            val radius = shapeSize / 2

                            val shapeOffset = (boxSize - shapeSize)/2

                            val outline = Stroke(8f)

                            val shapeCenter = Offset(boxSize/2, boxSize/2)

                            /*
                                Game logic implemented in Gem function
                                Attempted implementation in viewModel to introduce coroutine and
                                delays, but game logic would be infinitely called and would never
                                end. All game logic is present, but without delays in between for
                                user to see the process.
                             */

                            var newList = shapes.replaceEmptiesWithRandoms()

                            while(newList.matches.isNotEmpty()) {
                                handlers.onAddScore(newList.matches.size)
                                val removedMatches = newList.removeMatches(newList.matches)
                                val shifted = removedMatches.shiftDown()
                                val filled = shifted.replaceEmptiesWithRandoms()
                                newList = filled
                            }
                            updateGems(newList)

                            // Drawing shapes in grid
                            // Basis for grid code taken from the article "Jetpack Compose: building a generic grid canvas"
                            // on Medium
                            // Article: https://patxi.medium.com/jetpack-compose-building-a-generic-grid-canvas-557da35493fe
                            // GitHub Gist: https://gist.github.com/patxibocos/5dec2aa0df4fa060da4b873cbb072b0c

                            translate(boxSize, 0f) {
                                for (row in 0 until 8) {
                                    for (column in 0 until 8) {
                                        translate(
                                            left = column * boxSize,
                                            top = row * boxSize
                                        ) {
                                            val shape = shapes[row+1, column+1]
                                            setOffset(row, column, boxSize)
                                            when(shape.shapeType) {
                                                Circle -> {
                                                    val outlineColor =
                                                        if (shape.shapeType == highlightShapeType) Color.Magenta else Color.Black
                                                    drawCircle(color = Color.Yellow, center = shapeCenter, radius = radius)
                                                    drawCircle(color = outlineColor, center = shapeCenter, radius = radius, style = outline)
                                                }
                                                Square ->  {
                                                    val outlineColor =
                                                        if (shape.shapeType == highlightShapeType) Color.Magenta else Color.Black
                                                    drawRect(color = Color.Blue, size = Size(shapeSize, shapeSize), topLeft = Offset(shapeOffset, shapeOffset))
                                                    drawRect(color = outlineColor, size = Size(shapeSize, shapeSize), topLeft = Offset(shapeOffset, shapeOffset), style = outline)
                                                }
                                                Diamond -> {
                                                    val outlineColor =
                                                        if (shape.shapeType == highlightShapeType) Color.Magenta else Color.Black
                                                    drawDiamond(outlineColor, shapeSize, boxSize, outline)
                                                }
                                                Cross -> {
                                                    val outlineColor =
                                                        if (shape.shapeType == highlightShapeType) Color.Magenta else Color.Black
                                                    drawCross(outlineColor, shapeSize, boxSize, outline)
                                                }
                                                Empty -> {}
                                            }
                                        }
                                    }
                                }
                            }

                            // Drawing shape when dragged
                            when(drag.shapeType) {
                                Circle -> {
                                    val outlineColor =
                                        if (drag.shapeType == highlightShapeType) Color.Magenta else Color.Black
                                    translate(left = boxSize + drag.offset.x, top = drag.offset.y) {
                                        drawCircle(color = Color.Yellow, center = shapeCenter, radius = radius)
                                        drawCircle(color = outlineColor, center = shapeCenter, radius = radius, style = outline)
                                    }
                                }
                                Square ->  {
                                    val outlineColor =
                                        if (drag.shapeType == highlightShapeType) Color.Magenta else Color.Black
                                    drawRect(color = Color.Blue, size = Size(shapeSize, shapeSize), topLeft = Offset(drag.offset.x + boxSize, drag.offset.y))
                                    drawRect(color = outlineColor, size = Size(shapeSize, shapeSize), topLeft = Offset(drag.offset.x + boxSize, drag.offset.y), style = outline)
                                }
                                Diamond -> {
                                    val outlineColor =
                                        if (drag.shapeType == highlightShapeType) Color.Magenta else Color.Black
                                    translate(left = boxSize + drag.offset.x, top = drag.offset.y) {
                                        drawDiamond(outlineColor, shapeSize, boxSize, outline)
                                    }
                                }
                                Cross -> {
                                    val outlineColor =
                                        if (drag.shapeType == highlightShapeType) Color.Magenta else Color.Black
                                    translate(left = boxSize + drag.offset.x, top = drag.offset.y) {
                                        drawCross(outlineColor, shapeSize, boxSize, outline)

                                    }
                                }
                                Empty -> {}
                            }
                        }
                    }
                }
            }
        )
    }
}