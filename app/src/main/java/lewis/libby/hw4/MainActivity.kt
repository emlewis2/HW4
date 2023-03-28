package lewis.libby.hw4

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.*
import lewis.libby.hw4.ui.theme.HW4Theme
import kotlin.math.min
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<GemViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HW4Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    Ui(viewModel)
                }
            }
        }
    }
}

data class Handlers(
    val onAddScore: (points: Int) -> Unit,
    val gameLogic: (shapeList: List<Shape>) -> Unit,
//    val onDragStart: (finger: Offset, size: Float) -> Unit,
//    val onDrag: (Offset) -> Unit,
//    val onDragEnd: () -> Unit,
    val onHighLightShape: (finger: Offset, size: Float) -> Unit,
    val onDragStart: (finger: Offset, size: Float) -> Unit,
    val onDrag: (Offset) -> Unit,
    val onDragEnd: (offset: Offset, size: Float) -> Unit,
    val onPause: () -> Unit
)

@Composable
fun Ui(
    viewModel: GemViewModel
) {
    val scope = rememberCoroutineScope()
    val score = viewModel.score.collectAsState(initial = 0).value
    val shapes by viewModel.shapes.collectAsState(initial = List(64){Shape(Empty, Offset.Zero)})
    val highlightShapeType by viewModel.highlightShapeType.collectAsState(initial = null)

//    val location = shapeIndex(0, 0)
//    val test = shapes[1, 1]
//    Log.d("Test above", test.toString())

    val handlers = remember(viewModel) {
        Handlers(
            onAddScore = { viewModel.increaseScore(it) },
            gameLogic = {shapeList ->
                    scope.launch {
                        viewModel.gameLogic(shapeList)
                    }
            },
//            onDragStart = { finger, size -> viewModel.startDrag(finger, size) },
//            onDrag = { offset -> viewModel.drag(offset) },
//            onDragEnd = { viewModel.endDrag() },
            onHighLightShape = { finger, size ->
                scope.launch{
                    viewModel.highlightShape(finger, size)
                }
            },
            onDragStart = { finger, size -> viewModel.startDrag(finger, size) },
            onDrag = { offset -> viewModel.drag(offset) },
            onDragEnd = { offset, size -> viewModel.endDrag(offset, size) },
            onPause = { viewModel.onPause() }
        )
    }

    Graph(
        scope = scope,
        score = score,
        shapes = shapes,
        handlers = handlers,
        updateGems = viewModel::updateGems,
        highlightShapeType = highlightShapeType,
//        updateShapes = viewModel::updateShapes,
        modifier = Modifier.fillMaxSize()
    )

//    handlers.gameLogic(shapes)
}

@Composable
fun Graph(
    scope: CoroutineScope,
    score: Int,
    shapes: List<Shape>,
    updateGems: (List<Shape>) -> Unit,
    handlers: Handlers,
    highlightShapeType: ShapeType?,
//    updateShapes: (newShapes: List<Shape>) -> Unit,
    modifier: Modifier
) {
    with(LocalDensity.current) {

//        const val NUMBER_OF_ROWS = 8
//        val scope = rememberCoroutineScope()

        var finger by remember { mutableStateOf(Offset.Zero) }

//        fun DrawScope.drawCircle(x: Float, y: Float, outlineColor: Color, shapeOffsetPx: Float, shapeCenter: Offset, radius: Float, outline: DrawStyle) {
//            translate(x + shapeOffsetPx, y + shapeOffsetPx) {
//                drawCircle(color = Color.Green, center = shapeCenter, radius = radius, style = Fill)
//                drawCircle(color = outlineColor, center = shapeCenter, radius = radius, style = outline)
//            }
//        }

//        val testList = List(5) {Circle}
//        val currentPosition = testList[0,0]
//        Log.d("Test", currentPosition.toString())

//        val currentPosition = shapes[0, 0]
//        Log.d("Current Position", currentPosition.toString())

//        fun DrawScope.drawCircle(x: Float, y: Float, outlineColor: Color) {
//            translate(x + shapeOffsetPx, y + shapeOffsetPx) {
//                drawCircle(color = Color.Green, center = shapeCenter, radius = radius, style = Fill)
//                drawCircle(color = outlineColor, center = shapeCenter, radius = radius, style = outline)
//            }
//        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.title)) },
//                    actions = {
//                        IconButton(
//                            onClick = { handlers.onAddScore(5) }
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Add,
//                                contentDescription = "add"
//                            )
//                        }
//                    }
                )
            },
            content = { padding ->
                Column(
                    modifier = Modifier
                ) {
                    Row(
                        //                    modifier = Modifier.fillMaxSize(),
                        modifier = Modifier
                            //                        .size(width = width.toDp(), height = height.toDp()/3)
                            .fillMaxWidth()
                            .padding(vertical = 50.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        //                    Column(
                        //                        horizontalAlignment = Alignment.CenterHorizontally
                        //                    ) {
                        Text(
                            "Score: $score",
                            //                            textAlign = TextAlign.Center,
                            //                        modifier = Modifier.width(150.dp),
                            fontSize = 65.sp,
                            //                            modifier = Modifier.padding(16.dp)
                        )
                        //                    }
                    }
                    Row(
                        modifier = Modifier
//                            .padding(vertical = 50.dp)
                    ) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(handlers) {
                                    detectTapDragGestures(
                                        onTap = {
                                            Log.d("OnTap", "")
                                            finger = it
                                            val boxSize = min(size.width, size.height)/10f
                                            if ((it.x < boxSize) || (it.x > 9*boxSize)) {
                                                Log.d("x", "")
                                                handlers.onPause()
                                            } else if ((it.y > boxSize*8)){
                                                Log.d("y", "")
                                                handlers.onPause()
                                            } else {
                                                handlers.onHighLightShape(
                                                    finger, boxSize
                                                )
                                            }
                                        },
                                        onDragStart = { offset ->
                                            handlers.onDragStart(offset, min(size.width, size.height)/10f)
                                        },
                                        onDrag = { change, _ ->
                                            finger = change.position
                                            handlers.onDrag(change.position)
                                        },
                                        onDragEnd = {
                                            handlers.onDragEnd(finger, min(size.width, size.height)/10f)
                                        },
                                        onDragCancel = {
                                            handlers.onDragEnd(finger, min(size.width, size.height)/10f)
                                        },
                                    )
                                }
//                                .height(100.dp)
                            //                            .padding(vertical = 50.dp)
                        ) {
                            val minDim = min(size.width, size.height)
                            val boxSize = minDim/10f
                            val shapeSize = boxSize * 2/3
                            val radius = shapeSize / 2

                            val shapeOffset = (boxSize - shapeSize)/2

                            val outline = Stroke(8f)

                            val shapeCenter = Offset(boxSize/2, boxSize/2)

//                            drawRect(color = Color.Gray, topLeft = Offset.Zero)

//                            drawLine(
//                                start = Offset(x = size.width, y = 0f),
//                                end = Offset(x = 0f, y = size.height),
//                                color = Color.Blue
//                            )
//
//                            drawLine(
//                                start = Offset(x = 0f, y = 0f),
//                                end = Offset(x = size.width/10, 0f),
//                                color = Color.Red,
//                            )

//                            fun Size.toIntSize(): IntSize = IntSize(width.toInt(), height.toInt())
//
//                            val contentAlignment = Alignment.CenterHorizontally
////                            val alignOffset = contentAlignment.align(
////                                Size(
////                                    8 * boxSize,
////                                    8 * boxSize
////                                ).toIntSize(),
////                                size.toIntSize(), layoutDirection
////                            )
//                            val alignOffset = contentAlignment.align(
//                                size = size.width.toInt(),
//                                space = size.width.toInt(),
//                                layoutDirection = layoutDirection
//                            )

//                            val currentPosition = shapes.get(0, 0)
//                            Log.d("Current Position", currentPosition.toString())

//                            val outlineColor = Color.Black

//                            var newList = shapes.replaceEmptiesWithRandoms()

//                            if (!scope.isActive){
//                                handlers.gameLogic(shapes, scope)
//                            }
//                            handlers.gameLogic(shapes, scope)
//                            Log.d("New List", newList.toString())
//                            scope.launch{
//                                while(newList.matches.isNotEmpty()) {
//                                    Log.d("Matches", newList.matches.toString())
//                                    val removedMatches = newList.removeMatches(newList.matches)
//                                    delay(500)
//                                    Log.d("Removed", removedMatches.toString())
//                                    val shifted = removedMatches.shiftDown()
//                                    delay(500)
//                                    Log.d("Shifted", shifted.toString())
//                                    val filled = shifted.replaceEmptiesWithRandoms()
//                                    delay(500)
//                                    Log.d("Filled", filled.toString())
//                                    newList = filled
//                                    Log.d("Final", newList.toString())
//                                }
//                            }
//                            updateGems(newList)
                            var newList = shapes.replaceEmptiesWithRandoms()
                            Log.d("New List", newList.toString())
                            Log.d("Matches Outside", newList.matches.toString())

                            while(newList.matches.isNotEmpty()) {
                                Log.d("Matches", newList.matches.toString())
                                handlers.onAddScore(newList.matches.size)
                                val removedMatches = newList.removeMatches(newList.matches)
                                Log.d("Removed", removedMatches.toString())
                                val shifted = removedMatches.shiftDown()
                                Log.d("Shifted", shifted.toString())
                                val filled = shifted.replaceEmptiesWithRandoms()
                                Log.d("Filled", filled.toString())
                                newList = filled
                                Log.d("Final", newList.toString())
                            }
                            updateGems(newList)

//                            val newList = List(64){Circle}
//                            val matches = newList.matches
//                            val removedMatches = newList.removeMatches(matches)
//                            Log.d("Matches", matches.toString())
//                            Log.d("Removed", removedMatches.toString())
//                            val shifted = removedMatches.shiftDown()
//                            Log.d("")
//                            updateGems(newList)


                            translate(boxSize, 0f) {
                                for (row in 0 until 8) {
                                    for (column in 0 until 8) {
                                        translate(
                                            left = column * boxSize,
                                            top = row * boxSize
                                        ) {
//                                            Log.d("Inside", row.toString())
//                                            val currentPosition = shapes.get(row, column)
//                                            Log.d("Shape", currentPosition.toString())
                                            val shape = shapes[row+1, column+1]
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
                                                    drawRect(color = Color.Red, size = Size(shapeSize, shapeSize), topLeft = Offset(shapeOffset, shapeOffset))
                                                    drawRect(color = outlineColor, size = Size(shapeSize, shapeSize), topLeft = Offset(shapeOffset, shapeOffset), style = outline)
                                                }
                                                Cross -> {
                                                    val outlineColor =
                                                        if (shape.shapeType == highlightShapeType) Color.Magenta else Color.Black
                                                    drawCircle(color = Color.Green, center = shapeCenter, radius = radius)
                                                    drawCircle(color = outlineColor, center = shapeCenter, radius = radius, style = outline)
                                                }
                                                Empty -> {}
                                            }
//                                            Log.d("Inside", row.toString())
//                                            drawRect(color = Color.Blue, size = Size(shapeSize, shapeSize), topLeft = Offset(shapeOffset, shapeOffset))
//                                            drawCircle(color = Color.Green, center = shapeCenter, radius = radius)

                                        }
                                    }
                                }
                            }



//                            shapes.forEach {
//                                val outlineColor = Color.Black
//                                when (it.shapeType) {
//                                    Circle -> drawCircle(it.offset.x, it.offset.y, outlineColor, shapeOffset, shapeCenter, radius, outline)
////                                    Circle -> drawCircle(it.offset.x, it.offset.y, outlineColor)
//                                }
//                            }
//                            Log.d("Original Shapes", shapes.toString())
//                            var newList = shapes.replaceEmptiesWithRandoms()
//                            while(newList.matches.isNotEmpty()) {
//                                val removedMatches = newList.removeMatches(newList.matches)
//                                val shifted = removedMatches.shiftDown()
//                                val filled = shifted.replaceEmptiesWithRandoms()
//                                newList = filled
//                            }
//                            Log.d("New List", newList.toString())
//                            updateShapes(newList)
//                            Log.d("Shapes", shapes.toString())

//                            val removedMatches = newList.removeMatches(newList.matches)

//                            val shifted = removedMatches.shiftDown()

//                            updateGems(newList)
//                            Log.d("Updated Gems", shapes.toString())


//                            newList.forEach {
//                                val outlineColor = Color.Black
//                                when (it) {
//                                    Circle -> drawCircle(color = Color.Yellow)
//                                    Square -> drawRect(color = Color.Blue)
//                                    Empty -> {}
//                                    Diamond -> drawCircle(color = Color.Red)
//                                    Cross -> drawCircle(color = Color.Green)
//                                }
//                            }

                        }
                    }
                }
            }
        )
    }
}

// Gesture detector for taps and drags
// Copied from DragGestureDetector.kt in Jetpack Compose
// LICENSE: Apache 2.0
/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
suspend fun PointerInputScope.detectTapDragGestures(
    onTap: (Offset) -> Unit = {},
    onDragStart: (Offset) -> Unit = { },
    onDragEnd: () -> Unit = { },
    onDragCancel: () -> Unit = { },
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit = { _, _ ->}
) {
    forEachGesture {
        awaitPointerEventScope {
            val down = awaitFirstDown(requireUnconsumed = false)
            var drag: PointerInputChange?
            var overSlop = Offset.Zero
            do {
                drag = awaitTouchSlopOrCancellation(
                    down.id,
//                    down.type
                ) { change, over ->
                    change.consume()
                    overSlop = over
                }
            } while (drag != null && !drag.isConsumed)
            if (drag != null) {
                onDragStart.invoke(down.position) // Changed to down instead of drag
                onDrag(drag, overSlop)
                if (
                    !drag(drag.id) {
                        onDrag(it, it.positionChange())
                        it.consume()
                    }
                ) {
                    onDragCancel()
                } else {
                    onDragEnd()
                }
            } else {                    // ADDED
                onTap(down.position)    // ADDED
            }
        }
    }
}
