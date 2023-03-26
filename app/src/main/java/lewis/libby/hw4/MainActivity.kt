package lewis.libby.hw4

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lewis.libby.hw4.ui.theme.HW4Theme
import kotlin.math.min

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
    val onClickScore: (points: Int) -> Unit
)

@Composable
fun Ui(
    viewModel: GemViewModel
) {
    val score = viewModel.score.collectAsState(initial = 0).value
    val shapes = viewModel.shapes.collectAsState(initial = emptyList()).value

    val handlers = remember(viewModel) {
        Handlers(
            onClickScore = { viewModel.increaseScore(it) }
        )
    }

    Graph(
        score = score,
        shapes = shapes,
        handlers = handlers,
//        updateShapes = viewModel::updateShapes,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun Graph(
    score: Int,
    shapes: List<Shape>,
    handlers: Handlers,
//    updateShapes: (newShapes: List<Shape>) -> Unit,
    modifier: Modifier
) {
    with(LocalDensity.current) {

//        const val NUMBER_OF_ROWS = 8

        fun DrawScope.drawCircle(x: Float, y: Float, outlineColor: Color, shapeOffsetPx: Float, shapeCenter: Offset, radius: Float, outline: DrawStyle) {
            translate(x + shapeOffsetPx, y + shapeOffsetPx) {
                drawCircle(color = Color.Green, center = shapeCenter, radius = radius, style = Fill)
                drawCircle(color = outlineColor, center = shapeCenter, radius = radius, style = outline)
            }
        }

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
                    actions = {
                        IconButton(
                            onClick = { handlers.onClickScore(5) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "add"
                            )
                        }
                    }
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
                            fontSize = 75.sp,
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

                            drawRect(color = Color.Gray, topLeft = Offset.Zero)

                            drawLine(
                                start = Offset(x = size.width, y = 0f),
                                end = Offset(x = 0f, y = size.height),
                                color = Color.Blue
                            )

                            drawLine(
                                start = Offset(x = 0f, y = 0f),
                                end = Offset(x = size.width/10, 0f),
                                color = Color.Red,
                            )

                            fun Size.toIntSize(): IntSize = IntSize(width.toInt(), height.toInt())

                            val contentAlignment = Alignment.Center
                            val alignOffset = contentAlignment.align(
                                Size(
                                    8 * boxSize,
                                    8 * boxSize
                                ).toIntSize(),
                                size.toIntSize(), layoutDirection
                            )

                            translate(alignOffset.x.toFloat(), alignOffset.y.toFloat()) {
                                for (i in 0 until 8) {
                                    for (j in 0 until 8) {
                                        translate(
                                            left = j * boxSize,
                                            top = i * boxSize
                                        ) {
                                            Log.d("Inside", i.toString())
                                            drawRect(color = Color.Blue, size = Size(boxSize, boxSize))
                                            drawCircle(color = Color.Green, center = shapeCenter, radius = radius)
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
                            Log.d("Original Shapes", shapes.toString())
                            val newList = shapes.replaceEmptiesWithRandoms()
                            Log.d("New List", newList.toString())
//                            updateShapes(newList)
                            Log.d("Shapes", shapes.toString())

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