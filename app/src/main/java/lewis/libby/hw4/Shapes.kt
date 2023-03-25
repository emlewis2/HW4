package lewis.libby.hw4

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset

sealed interface ShapeType
object Circle: ShapeType
//object Square: ShapeType
//object Diamond: ShapeType
//object Cross: ShapeType
//object Empty: ShapeType

@Immutable
data class Shape(
    val shapeType: ShapeType,
    val offset: Offset
)