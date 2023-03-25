package lewis.libby.hw4

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.Px
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
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
                    // Tutorial used to get Display Metrics
                    // https://www.tutorialkart.com/kotlin-android/get-screen-width-and-height-programmatically-example/
                    val displayMetrics = DisplayMetrics()
                    windowManager.defaultDisplay.getMetrics(displayMetrics)
                    val width = displayMetrics.widthPixels
                    val height = displayMetrics.heightPixels
                    Ui(viewModel, width, height)
                }
            }
        }
    }
}

data class Handlers(
    val onClickScore: (shape: Shape) -> Unit
)

@Composable
fun Ui(
    viewModel: GemViewModel,
    screenWidth: Int,
    screenHeight: Int
) {
    val score = viewModel.score.collectAsState(initial = 0).value
    val shapes = viewModel.shapes.collectAsState(initial = emptyList()).value

    val handlers = remember(viewModel) {
        Handlers(
            onClickScore = { viewModel.addShape(it) }
        )
    }

//    val smallestDimension = min(screenWidth.dp, screenHeight.dp)
//    val shapeBoxSize = screenWidth.dp / 10
//    Log.d("ShapeBoxSize", shapeBoxSize.toString())
//    val shapeSize = shapeBoxSize * 2/3
//    Log.d("ShapeSize", shapeSize.toString())

    Graph(
//        shapeSizeDp = shapeSize,
//        shapeOutlineWidthDp = 3.dp,
//        shapeBoxSizeDp = shapeBoxSize,
        score = score,
        shapes = shapes,
        handlers = handlers,
        screenWidth = screenWidth,
        screenHeight = screenHeight,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun Graph(
//    shapeSizeDp: Dp,
//    shapeOutlineWidthDp: Dp,
//    shapeBoxSizeDp: Dp,
    score: Int,
    shapes: List<Shape>,
    handlers: Handlers,
    screenWidth: Int,
    screenHeight: Int,
    modifier: Modifier
) {
    with(LocalDensity.current) {

//        Log.d("Width", screenWidth.toString())
//        Log.d("Height", screenHeight.toString())


//        val shapeSizePx = shapeSizeDp.toPx()
//        val shapeOutlineWidthPx = shapeOutlineWidthDp.toPx()
//        val shapeBoxSizePx = shapeBoxSizeDp.toPx()
//        val shapeOffsetPx = (shapeBoxSizePx - shapeSizePx) / 2
//        val radius = shapeSizePx / 2
//        Log.d("Radius", radius.toString())
//
//        val shapeCenter = remember(shapeSizePx) {
//            Offset(shapeSizePx/2, shapeSizePx/2)
//        }
//
//        val shapeSize = remember(shapeSizePx) {
//            Size(shapeSizePx, shapeSizePx)
//        }
//
//        val shapeBoxSize = remember(shapeBoxSizePx) {
//            Size(shapeBoxSizePx, shapeBoxSizePx)
//        }
//
//        val halfShapeBoxOffset = remember(shapeBoxSizePx) {
//            Offset(shapeBoxSizePx/2, shapeBoxSizePx/2)
//        }
//
//        val outline = remember(shapeOutlineWidthPx) {
//            Stroke(shapeOutlineWidthPx)
//        }
//
//        fun Shape.getCenter() = offset + halfShapeBoxOffset




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
                            onClick = { handlers.onClickScore(Shape(shapeType = Circle, offset = Offset.Zero)) }
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
                            .padding(vertical = 50.dp)
                    ) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
//                                .height(100.dp)
                            //                            .padding(vertical = 50.dp)
                        ) {
                            val canvasWidth = size.width
                            val canvasHeight = size.height
                            val minDim = min(size.width, size.height)
                            Log.d("MinDimFloat", minDim.toString())
                            val boxSize = minDim/10f
                            Log.d("BoxSize", boxSize.toString())
                            val shapeSize = boxSize * 2/3
                            Log.d("ShapeSize", shapeSize.toString())
                            val radius = shapeSize / 2
                            Log.d("RadiusNow", radius.toString())

                            drawCircle(color = Color.LightGray, radius = radius, center = Offset.Zero)

//                            drawCircle(color = Color.LightGray, radius = )

//                            Log.d("MinDim", minDim.toString())
//                            val minDimPx = minDim.toPx()
////                            val shapeBoxSizeDp = (minDim / 10)
//                            Log.d("Box Dp", shapeBoxSizeDp.toString())
//                            val shapeBoxSizePx = minDimPx/10f
//                            Log.d("Box Size", shapeBoxSizePx.toString())
//                            val shapeSizePx = shapeBoxSizePx
//                            Log.d("Shape Size", shapeSizePx.toString())
//                            val shapeOutlineWidthPx = 3.dp.toPx()
                            val shapeOffsetPx = (boxSize - shapeSize)/2
//                            val radius = shapeSizePx / 2
//
                            val outline = Stroke(8f)
//
                            val shapeCenter = Offset(shapeSize/2, shapeSize/2)
//
//                            val shapeBoxSize = Size(shapeBoxSizePx, shapeBoxSizePx)
//
//                            val shapeSize = Size(shapeSizePx, shapeSizePx)

                            drawRect(color = Color.Gray, topLeft = Offset.Zero)
//                            drawRect(color = Color.Blue, topLeft = Offset.Zero, size = Size(shapeBoxSizePx)

                            drawLine(
                                start = Offset(x = size.width, y = 0f),
                                end = Offset(x = 0f, y = size.height),
                                color = Color.Blue
                            )

                            Log.d("Width Test", (size.width/10).toString())

                            drawLine(
                                start = Offset(x = 0f, y = 0f),
                                end = Offset(x = size.width/10, 0f),
                                color = Color.Red,
                            )

                            shapes.forEach {
                                val outlineColor = Color.Black
                                when (it.shapeType) {
                                    Circle -> drawCircle(it.offset.x, it.offset.y, outlineColor, shapeOffsetPx, shapeCenter, radius, outline)
//                                    Circle -> drawCircle(it.offset.x, it.offset.y, outlineColor)
                                }
                            }
                        }
                    }
                }
            }
        )



    }
}