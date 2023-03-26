package lewis.libby.hw4

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class GemViewModel: ViewModel() {
    private val _score = MutableStateFlow<Int>(0)
    val score: Flow<Int>
        get() = _score

    private val initialShapeList: List<Shape> = List(64) {Empty}
    private val _shapes = MutableStateFlow<List<Shape>>(initialShapeList)
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
}