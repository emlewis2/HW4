package lewis.libby.hw4.screens

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.launch
import lewis.libby.hw4.*

@Composable
fun Ui(
    viewModel: GemViewModel
) {
    // View model variables
    val scope = rememberCoroutineScope()
    val score = viewModel.score.collectAsState(initial = 0).value
    val shapes by viewModel.shapes.collectAsState(initial = List(64){ Shape(Empty, Offset.Zero) })
    val highlightShapeType by viewModel.highlightShapeType.collectAsState(initial = null)
    val drag by viewModel.drag.collectAsState(initial = Shape(Empty, Offset.Zero))

    // Handlers instance
    val handlers = remember(viewModel) {
        Handlers(
            onAddScore = { viewModel.increaseScore(it) },
            onHighLightShape = { finger, size ->
                scope.launch{
                    viewModel.highlightShape(finger, size)
                }
            },
            onDragStart = { finger, size -> viewModel.startDrag(finger, size) },
            onDrag = { offset -> viewModel.drag(offset) },
            onDragEnd = { offset, size -> viewModel.endDrag(offset, size) },
            onPause = { viewModel.onPause() }
        )
    }

    // Switch between game screen and pause screen
    when(viewModel.screen) {
        is GemScreen -> Gems(
            score = score,
            shapes = shapes,
            drag = drag,
            handlers = handlers,
            updateGems = viewModel::updateGems,
            highlightShapeType = highlightShapeType,
            setOffset = viewModel::setOffset,
        )
        is PauseScreen -> Pause(viewModel::onPlay)
    }
}