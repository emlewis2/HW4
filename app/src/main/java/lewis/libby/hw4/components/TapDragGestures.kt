package lewis.libby.hw4.components

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.positionChange

// Gesture detector for taps and drags
// Copied from DragGestureDetector.kt in Jetpack Compose
// LICENSE: Apache 2.0
/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Changes from the Module on the Compose Graph Example
suspend fun PointerInputScope.detectTapDragGestures(
    onTap: (Offset) -> Unit = {},
    onDragStart: (Offset) -> Unit = { },
    onDragEnd: () -> Unit = { },
    onDragCancel: () -> Unit = { },
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit = { _, _ ->}
) {
    forEachGesture {
        awaitPointerEventScope {
            val down = awaitFirstDown(requireUnconsumed = false)
            var drag: PointerInputChange?
            var overSlop = Offset.Zero
            do {
                drag = awaitTouchSlopOrCancellation(
                    down.id,
                ) { change, over ->
                    change.consume()
                    overSlop = over
                }
            } while (drag != null && !drag.isConsumed)
            if (drag != null) {
                onDragStart.invoke(down.position) // Changed to down instead of drag
                onDrag(drag, overSlop)
                if (
                    !drag(drag.id) {
                        onDrag(it, it.positionChange())
                        it.consume()
                    }
                ) {
                    onDragCancel()
                } else {
                    onDragEnd()
                }
            } else {                    // ADDED
                onTap(down.position)    // ADDED
            }
        }
    }
}