package lewis.libby.hw4

import android.app.Application
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

sealed interface Screen

object GemScreen: Screen

object PauseScreen: Screen

class GemViewModel: ViewModel() {
    var screen by mutableStateOf<Screen>(GemScreen)
        private set

    var shuffleFlag = 0

//    var screen by mutableStateOf<Screen>(GemScreen)
//        private set

    private val _highlightShapeType = MutableStateFlow<ShapeType?>(null)
    val highlightShapeType: Flow<ShapeType?>
        get() = _highlightShapeType

    private val _score = MutableStateFlow<Int>(0)
    val score: Flow<Int>
        get() = _score

//    private val initialShapeList: List<Shape> = List(64) {Circle}
    private val _shapes = MutableStateFlow<List<Shape>>(List(64){Shape(Empty, Offset.Zero)})
    val shapes: Flow<List<Shape>>
        get() = _shapes

    fun increaseScore(points: Int) {
        _score.value = _score.value + points
    }

    fun addShape(shape: Shape) {
        _shapes.value = _shapes.value + shape
    }

//    fun updateShapes(newShapes: List<Shape>) {
////        _shapes.value = newShapes
//        Log.d("Updating shapes", _shapes.value.toString())
//    }

//    /**
//     * Calculate the List index of a gem based on its row and column.
//     */
//    private fun shapeIndex(row: Int, column: Int): Int {
//        require(row in 1..NUMBER_OF_ROWS)
//        require(column in 1..NUMBER_OF_ROWS)
//        return (row-1)* NUMBER_OF_ROWS + column - 1
//    }
//
//    /**
//     * "Get" operator that allows us to use `shapes[row, column]` for a more natural
//     * two-dimensional-array like access
//     */
//    operator fun List<Shape>.get(row: Int, column: Int) = this[shapeIndex(row, column)]
//
//    /**
//     * "Set" operator that allows us to use `shapes[row, column] = shape` for more natural
//     *    two-dimensional-array-like access
//     */
//    operator fun MutableList<Shape>.set(row: Int, column: Int, shape: Shape) {
//        this[shapeIndex(row, column)] = shape
//    }

    fun updateGems(newList: List<Shape>) {
//        Log.d("In Update Gems", "")
        _shapes.update{newList}
//        Log.d("In Update Gems Updated", _shapes.value.toString())
    }

    suspend fun gameLogic(shapeList: List<Shape>) = withContext(Dispatchers.Default) {
//        var newList = shapeList.replaceEmptiesWithRandoms()
        Log.d("In Game Logic", "")
//        if (!scope.isActive) {
            Log.d("New List", shapeList.toString())
//        shapeList = shapeList.replaceEmptiesWithRandoms()

            while (shapeList.matches.isNotEmpty()) {
                Log.d("Matches", shapeList.matches.toString())
                //Highlight here
                val removedMatches = shapeList.removeMatches(shapeList.matches)
                _shapes.update { removedMatches }
                delay(1000)
                Log.d("Removed", removedMatches.toString())
                val shifted = removedMatches.shiftDown()
                _shapes.update { shifted }
                delay(1000)
                Log.d("Shifted", shifted.toString())
                val filled = shifted.replaceEmptiesWithRandoms()
                _shapes.update { filled }
                delay(1000)
                Log.d("Filled", filled.toString())
            }
//        _shapes.update{newList}
//        }
    }

    private var dragShape: Shape? = null
    private var dragShapeOffset: Offset = Offset.Zero
    private var dragShapeRow: Int = 0
    private var dragShapeColumn: Int = 0

    fun startDrag(finger: Offset, size: Float) {
        // Find at can return null if clicked where no shape, but just sets dragShape to null and nothing happens
        // Using apply so that if something is found, then dragShapeOffset is changed
//        dragShape = _shapes.value.findAt(finger, size)?.apply {
////            dragShapeOffset = finger - offset
//            dragShapeOffset = finger
//        }

        Log.d("In startDrag", "")
        Log.d("finger", finger.toString())
        val intOffsetX = finger.x.toInt()
        Log.d("intX", intOffsetX.toString())
        val intOffsetY = finger.y.toInt()
        Log.d("intY", intOffsetY.toString())
        val row = intOffsetY / size.toInt()
        Log.d("row", row.toString())
        val column = intOffsetX / size.toInt()
        Log.d("column", column.toString())
        if (row in 0..7 && column >= 1 && column <= 8) {
            _shapes.value[row+1, column].let { shape ->
                dragShape = shape
                dragShapeOffset = finger
                dragShapeRow = row
                dragShapeColumn = column
            }
        } else {
            shuffleFlag = 1
        }
    }

    fun drag(offset: Offset) {
        dragShape?.let { shape ->
            val newShape = shape.copy(offset = offset - dragShapeOffset)

//            _shapes.value = _shapes.value - shape + newShape
            dragShape = newShape    // Need to update to newShape as the new instance or else are never removing the new shapes that are added
        }
    }

    fun endDrag(offset: Offset, size: Float) {
        var rightBound = 0f
        var leftBound = 0f
        var topBound = 0f
        var bottomBound = 0f
        if (dragShapeColumn == 8) {
            rightBound = size*(dragShapeColumn)
        } else {
            rightBound = size*(dragShapeColumn+1)
        }
        if (dragShapeColumn == 1) {
            leftBound = size*(dragShapeColumn)
        } else {
            leftBound = size*(dragShapeColumn-1)
        }
        if (dragShapeRow == 0) {
            topBound = size*0f
        } else {
            topBound = size*(dragShapeRow)
        }
        if (dragShapeRow ==7) {
            bottomBound = size*(dragShapeRow)
        } else {
            bottomBound = size*(dragShapeRow+1)
        }
        Log.d("In highlight", "")
        Log.d("finger", offset.toString())
        val intOffsetX = offset.x.toInt()
        Log.d("intX", intOffsetX.toString())
        val intOffsetY = offset.y.toInt()
        Log.d("intY", intOffsetY.toString())
        val row = intOffsetY / size.toInt()
        Log.d("row", row.toString())
        val column = intOffsetX / size.toInt()
        Log.d("column", column.toString())
        if (row in 0..7 && column >= 1 && column <= 8) {
            if (row == dragShapeRow-1 && column == dragShapeColumn) {
                switchShapes(row, column)
            } else if (row == dragShapeRow+1 && column == dragShapeColumn) {
                switchShapes(row, column)
            } else if (row == dragShapeRow && column == dragShapeColumn+1) {
                switchShapes(row, column)
            } else if (row == dragShapeRow && column == dragShapeColumn-1) {
                switchShapes(row, column)
            }
        } else if (shuffleFlag == 1){
            dragShape = null
            dragShapeColumn = 0
            dragShapeRow = 0
            shuffleFlag = 0
            shuffleGems()
        }
        dragShape = null
        dragShapeColumn = 0
        dragShapeRow = 0
    }

    private fun List<Shape>.findAt(offset:Offset, shapeBoxSizePx: Float) =
        reversed().find { shape ->
//            val normalized = offset
            val normalized = offset - shape.offset
            normalized.x >= 0 &&
                    normalized.y >=0 &&
                    normalized.x <= shapeBoxSizePx &&
                    normalized.y <= shapeBoxSizePx
        }

    suspend fun highlightShape(finger: Offset, size: Float) = withContext(Dispatchers.Default) {
        // Is nullable, so needs let to say if someone clicks where no shape, then no highlight happens
        Log.d("In highlight", "")
        Log.d("finger", finger.toString())
        val intOffsetX = finger.x.toInt()
        Log.d("intX", intOffsetX.toString())
        val intOffsetY= finger.y.toInt()
        Log.d("intY", intOffsetY.toString())
        val row = intOffsetY / size.toInt()
        Log.d("row", row.toString())
        val column = intOffsetX / size.toInt()
        Log.d("column", column.toString())
        if (row in 0..7 && column >= 1 && column <= 8) {
            _shapes.value[row+1, column].let {shape ->
                repeat(3) {
                    Log.d("Shape: ", shape.toString())
//                    Log.d("Location: ", _shapes.value[row+1, column].toString())
                    _highlightShapeType.value = shape.shapeType
                    Log.d("Next", _highlightShapeType.value.toString())
                    delay(500)
                    _highlightShapeType.value = null
                    if (it != 2) {
                        delay(500)
                    }
                }
            }
        }
    }

    fun onPause() {
        val yes = "hi"
        Log.d("On Pause", yes)
        this.screen = PauseScreen
    }

    fun onPlay() {
        this.screen = GemScreen
    }

    private fun shuffleGems() {
        Log.d("In shuffle", "")
        val newList = (List(64){Shape(Empty, Offset.Zero)})
        newList.replaceEmptiesWithRandoms()
        _shapes.update { newList }
        _score.value = _score.value - 10
    }

    private fun switchShapes(row: Int, column: Int) {
        _shapes.value[row+1, column].let { shape ->
            Log.d("DragShape", dragShape?.shapeType.toString())
            _shapes.value = _shapes.value.replace(row+1, column, dragShape ?: Shape(Empty, Offset.Zero))
            _shapes.value = _shapes.value.replace(dragShapeRow+1, dragShapeColumn, shape)
            Log.d("Other Shape", shape.shapeType.toString())
        }
        dragShape = null
        dragShapeColumn = 0
        dragShapeRow = 0
    }
}