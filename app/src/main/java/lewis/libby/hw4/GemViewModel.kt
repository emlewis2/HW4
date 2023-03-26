package lewis.libby.hw4

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class GemViewModel: ViewModel() {
    private val _score = MutableStateFlow<Int>(0)
    val score: Flow<Int>
        get() = _score

//    private val initialShapeList: List<Shape> = List(64) {Circle}
    private val _shapes = MutableStateFlow<List<Shape>>(List(64){Empty})
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
}