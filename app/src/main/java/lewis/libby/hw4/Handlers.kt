package lewis.libby.hw4

import androidx.compose.ui.geometry.Offset

// Handlers for action functions
data class Handlers(
    val onAddScore: (points: Int) -> Unit,
    val onHighLightShape: (finger: Offset, size: Float) -> Unit,
    val onDragStart: (finger: Offset, size: Float) -> Unit,
    val onDrag: (Offset) -> Unit,
    val onDragEnd: (offset: Offset, size: Float) -> Unit,
    val onPause: () -> Unit
)