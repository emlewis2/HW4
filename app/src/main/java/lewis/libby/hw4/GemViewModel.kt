package lewis.libby.hw4

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
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

    // Flag variable to track whether to shuffle pieces or not
    private var shuffleFlag = 0

    // Used to update drag
    private val _drag = MutableStateFlow(Shape(Empty, Offset.Zero))
    val drag: Flow<Shape>
        get() = _drag

    private val _highlightShapeType = MutableStateFlow<ShapeType?>(null)
    val highlightShapeType: Flow<ShapeType?>
        get() = _highlightShapeType

    private val _score = MutableStateFlow(0)
    val score: Flow<Int>
        get() = _score

    private val _shapes = MutableStateFlow(List(64){Shape(Empty, Offset.Zero)})
    val shapes: Flow<List<Shape>>
        get() = _shapes

    fun increaseScore(points: Int) {
        _score.value = _score.value + points
    }

    fun updateGems(newList: List<Shape>) {
        _shapes.update{newList}
    }

    /*
      Attempted to implement gameLogic in viewModel in order to run under coroutine
      However, ran into an issue where function would be called infinitely from the Gems function
      Would be called everytime the Canvas was redrawn
      Unable to find solution, so current implementation is in the main activity
      All game logic works in main activity but without delays
     */

//    suspend fun gameLogic(shapeList: List<Shape>) = withContext(Dispatchers.Default) {
//        val shapeList = shapeList.replaceEmptiesWithRandoms()
//
//            while (shapeList.matches.isNotEmpty()) {
//                //Highlight here
//                val removedMatches = shapeList.removeMatches(shapeList.matches)
//                _shapes.update { removedMatches }
//                delay(1000)
//                val shifted = removedMatches.shiftDown()
//                _shapes.update { shifted }
//                delay(1000)
//                val filled = shifted.replaceEmptiesWithRandoms()
//                _shapes.update { filled }
//                delay(1000)
//            }
//    }

    private var dragShape: Shape? = null        // Used for holding shape to potentially be switched
    private var dragShapeOffset: Offset = Offset.Zero
    private var dragShapeRow: Int = 0
    private var dragShapeColumn: Int = 0

    fun startDrag(finger: Offset, size: Float) {
        val intOffsetX = finger.x.toInt()
        val intOffsetY = finger.y.toInt()
        val row = intOffsetY / size.toInt()
        val column = intOffsetX / size.toInt()
        if (row in 0..7 && column >= 1 && column <= 8) {
            _shapes.value[row+1, column].let { shape ->
                dragShape = shape
                _drag.value = shape
                dragShapeOffset = finger - shape.offset
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
            _drag.value = newShape
            dragShape = newShape
        }
    }

    fun endDrag(offset: Offset, size: Float) {
        val intOffsetX = offset.x.toInt()
        val intOffsetY = offset.y.toInt()
        val row = intOffsetY / size.toInt()
        val column = intOffsetX / size.toInt()
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
        _drag.value = Shape(Empty, Offset.Zero)
    }

    suspend fun highlightShape(finger: Offset, size: Float) = withContext(Dispatchers.Default) {
        val intOffsetX = finger.x.toInt()
        val intOffsetY= finger.y.toInt()
        val row = intOffsetY / size.toInt()
        val column = intOffsetX / size.toInt()
        if (row in 0..7 && column >= 1 && column <= 8) {
            _shapes.value[row+1, column].let {shape ->
                repeat(3) {
                    _highlightShapeType.value = shape.shapeType
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
        this.screen = PauseScreen
    }

    fun onPlay() {
        this.screen = GemScreen
    }

    private fun shuffleGems() {
        val newList = (List(64){Shape(Empty, Offset.Zero)})
        newList.replaceEmptiesWithRandoms()
        _shapes.update { newList }
        /*
            Note that ten points are subtracted from the score, but any matches found upon shuffling
            are added to the score, so overall the score will likely increase (unless no matches
            found upon shuffling)
         */
        _score.value = _score.value - 10
    }

    private fun switchShapes(row: Int, column: Int) {
        _shapes.value[row+1, column].let { shape ->
            _shapes.value = _shapes.value.replace(row+1, column, dragShape ?: Shape(Empty, Offset.Zero))
            _shapes.value = _shapes.value.replace(dragShapeRow+1, dragShapeColumn, shape)
        }
        if (_shapes.value.matches.isEmpty()) {
            _shapes.value[dragShapeRow+1, dragShapeColumn].let { shape ->
                _shapes.value = _shapes.value.replace(row+1, column, shape)
                _shapes.value = _shapes.value.replace(dragShapeRow+1, dragShapeColumn, dragShape ?: Shape(Empty, Offset.Zero))
            }
        }
        dragShape = null
        dragShapeColumn = 0
        dragShapeRow = 0
    }

    // Set offset of a particular shape
    fun setOffset(row: Int, column: Int, boxSize: Float) {
        _shapes.value[row+1, column+1].let {shape ->
            _shapes.value = _shapes.value.replace(row+1, column+1, Shape(shape.shapeType, Offset(column*boxSize, row*boxSize)))

        }
    }
}