package lewis.libby.hw4

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class GemViewModel: ViewModel() {
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
        Log.d("In Update Gems", "")
        _shapes.update{newList}
        Log.d("In Update Gems Updated", _shapes.value.toString())
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

//    fun startDrag(finger: Offset, size: Float) {
//        // Find at can return null if clicked where no shape, but just sets dragShape to null and nothing happens
//        // Using apply so that if something is found, then dragShapeOffset is changed
//        dragShape = _shapes.value.findAt(finger, size)?.apply {
////            dragShapeOffset = finger - offset
//            dragShapeOffset = finger
//        }
//    }

//    fun drag(offset: Offset) {
//        dragShape?.let { shape ->
//            val newShape = shape.copy(offset = offset - dragShapeOffset)
//            _shapes.value = _shapes.value - shape + newShape
//            dragShape = newShape    // Need to update to newShape as the new instance or else are never removing the new shapes that are added
//        }
//    }

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
        val intOffsetY = finger.y.toInt()
        Log.d("intY", intOffsetY.toString())
        val row = intOffsetX / size.toInt()
        Log.d("row", row.toString())
        val column = intOffsetY / size.toInt()
        Log.d("column", column.toString())
        if (row in 1..8 && column > 0 && column < 9) {
            _shapes.value[row, column-2].let {shape ->
                repeat(3) {
                    Log.d("Shape: ", shape.toString())
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
//        _shapes.value.findAt(finger, size)?.let { shape ->
//            repeat(3) {
//                Log.d("Shape: ", shape.toString())
//                _highlightShapeType.value = shape.shapeType
//                delay(200)
//                _highlightShapeType.value = null
//                if (it != 2) {
//                    delay(200)
//                }
//            }
//        }
    }
}