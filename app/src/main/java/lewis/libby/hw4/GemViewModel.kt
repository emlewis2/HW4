package lewis.libby.hw4

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class GemViewModel: ViewModel() {
    private val _score = MutableStateFlow<Int>(0)
    val score: Flow<Int>
        get() = _score

    private val _shapes = MutableStateFlow<List<Shape>>(emptyList())
    val shapes: Flow<List<Shape>>
        get() = _shapes

    fun increaseScore(points: Int) {
        _score.value = _score.value + points
    }

    fun addShape(shape: Shape) {
        _shapes.value = _shapes.value + shape
    }
}