package lewis.libby.hw4

// Helper File from JHU Course EN.605.686.81.SP23 by Scott Stanchfield

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.delay
import kotlin.random.Random // (must be at top of file after package statement)

/**
 * Number of rows in the grid. Note that we assume the grid is square (i.e. has the same number
 *   of rows and columns)
 */
const val NUMBER_OF_ROWS = 8

/**
 * Shape types are represented as singleton objects that all derive from a common
 * sealed interface [Shape]. Using a sealed interface enforces that we have a finite
 * set of subtypes, and we can use these in an exhaustive `when` expression.
 *
 * You need to define your own shapes rather than the ones I have below, but be sure to keep
 *    [Empty]
 */
sealed interface ShapeType
object Square: ShapeType
object Circle: ShapeType
object Cross: ShapeType
object Diamond: ShapeType
object Empty: ShapeType

@Immutable
data class Shape(
    val shapeType: ShapeType,
    val offset: Offset
)

// Removed Shape Types to put in separate file

// The functions in this file assume that the grid of shapes is represented by a `List<Shape>`
//    that is sized `NUMBER_OF_ROWS * NUMBER_OF_ROWS`. [Empty] is used to mark spots in the
//    grid that do not currently contain a shape.
// The shapes list should be immutable so the UI knows when to refresh.
// (Note that the [shiftDown] function temporarily uses a mutable list internally as the
//   work it does would require a much more complex map chain.)

/**
 * Calculate the List index of a gem based on its row and column.
 */
fun shapeIndex(row: Int, column: Int): Int {
    require(row in 1..NUMBER_OF_ROWS)
    require(column in 1..NUMBER_OF_ROWS)
    return (row-1)* NUMBER_OF_ROWS + column - 1
}

/**
 * "Get" operator that allows us to use `shapes[row, column]` for a more natural
 * two-dimensional-array like access
 */
operator fun List<Shape>.get(row: Int, column: Int) = this[shapeIndex(row, column)]

//fun List<Shape>.getShape(row: Int, column: Int) = this.get(shapeIndex(0, 0))

/**
 * "Set" operator that allows us to use `shapes[row, column] = shape` for more natural
 *    two-dimensional-array-like access
 */
operator fun MutableList<Shape>.set(row: Int, column: Int, shape: Shape) {
    this[shapeIndex(row, column)] = shape
}

/**
 * Helper function that converts a `List<Shape>` into a new `List<Shape>` state with the indicated
 *    piece replaced. Note that this function returns a new list.
 */
fun List<Shape>.replace(row: Int, column: Int, shape: Shape): List<Shape> {
    val index = shapeIndex(row, column)
    return mapIndexed { n, existingShape ->
        if (n == index) shape else existingShape
    }
}

/**
 * Get a list of indexes that represent gems that are in matches. There are other much more optimal
 *   ways to do this, but this is good for a quick assignment algorithm...
 * Note that we temporarily use a mutable set to add matches to and convert it back to an immutable
 *   list at the end
 * Note that this is a property, so you'll call `shapes.matches` to get the list of matches
 */
val List<Shape>.matches: Set<Int>
    get() {
        val matches = mutableSetOf<Int>()
        (1..NUMBER_OF_ROWS).forEach { i ->
            (1..NUMBER_OF_ROWS - 2).forEach { j ->
                // check horizontal
                val shape1Horizontal = this[i, j].shapeType
                val shape2Horizontal = this[i, j + 1].shapeType
                val shape3Horizontal = this[i, j + 2].shapeType
                if (shape1Horizontal == shape2Horizontal && shape1Horizontal == shape3Horizontal) {
                    matches.add(shapeIndex(i, j))
                    matches.add(shapeIndex(i, j + 1))
                    matches.add(shapeIndex(i, j + 2))
                }
                // check vertical
                val shape1Vertical = this[j, i].shapeType
                val shape2Vertical = this[j+1, i].shapeType
                val shape3Vertical = this[j+2, i].shapeType
                if (shape1Vertical == shape2Vertical && shape1Vertical == shape3Vertical) {
                    matches.add(shapeIndex(j, i))
                    matches.add(shapeIndex(j+1, i))
                    matches.add(shapeIndex(j+2, i))
                }
            }
        }
        return matches.toSet() // create an immutable set out of it so the caller can't abuse it
    }

/**
 * Shift down pieces to fill in empty spots.
 * Note that we temporarily use a mutable copy of the gem list to make our job easier, then
 *   convert it to an immutable list at the end
 */
fun List<Shape>.shiftDown(): List<Shape> {
    /**
     * Nested helper that swaps the gems at two positions in a MUTABLE copy of the board.
     * Note that this is an unfortunate idiom in kotlin for swapping. It's the equivalent of
     *    ```
     *       val temp = this[row2, column2]
     *       this[row2, column2] = this[row1, column1]
     *       this[row1, column1] = temp
     *    ```
     *
     * It works, but it's crazy confusing unless you've really gotten used to kotlin. I only show
     *   it here because you're likely to see it somewhere...
     * Otherwise, I'd recommend the above explicit code
     *
     * Here's what's happening
     *    1. `this[row2, column2].also {...}` makes a copy of `this[row2, column2]`, passing it
     *       into the lambda as `this`. We're using that `this` as the temporary copy of the
     *       second shape
     *    2. The `{ this[row2, column2] = this[row1, column1] }` lambda runs,
     *       doing exactly what it says
     *    3. The `also` returns its `this` (which was that temp copy of the second shape)
     *    4. `this[row1, column1]` is set to that (which was the old value of the second shape)
     *
     * Yes, it works, but I don't recommend writing code like this. However, take a few moments to
     *   read the above carefully and if you have questions, let me know. You will see this at
     *   some point and I would rather you actually understand what it's doing than just say
     *   "oh, that's a kotlin idiom that swaps and I don't get it"...
     */
    fun MutableList<Shape>.swapMutable(row1: Int, column1: Int, row2: Int, column2: Int) {
        this[row1, column1] = this[row2, column2].also { this[row2, column2] = this[row1, column1] }
    }

    val mutable = toMutableList() // create a temp mutable list with our elements
    for(column in 1..NUMBER_OF_ROWS) {
        for(row in NUMBER_OF_ROWS downTo 2) { // if top is empty, nothing to move into it...
            val shape = mutable[row, column]
            if (shape.shapeType == Empty) {
                // find first non-empty above it and swap
                for(above in row-1 downTo 1) {
                    if (mutable[above, column].shapeType != Empty) {
                        mutable.swapMutable(row, column, above, column)
                        break
                    }
                }
            }
        }
    }
    return mutable.toList() // create an immutable list out of it so the caller can't abuse it
}

/**
 * Create a copy of the gem list that has [Empty] gems where matching gems were
 */
 fun List<Shape>.removeMatches(matches: Set<Int>) =
    mapIndexed { n, existingShape ->
        if (n in matches) {
            Shape(Empty, Offset.Zero)
        } else {
            existingShape
        }
//        delay(500)
    }

/**
 * Create a copy of the gem list that has random shapes where [Empty] spaces used to be
 */
fun List<Shape>.replaceEmptiesWithRandoms() =
    map {
        if (it.shapeType == Empty) {
            randomShape()
        } else {
            it
        }
    }

private val random = Random(System.currentTimeMillis())

/**
 * Create a random shape. Note that you'll need to replace these with your shapes
 */
fun randomShape() = when(random.nextInt(4)) {
    0 -> Shape(Square, Offset.Zero)
    1 -> Shape(Circle, Offset.Zero)
    2 -> Shape(Cross, Offset.Zero)
    else -> Shape(Diamond, Offset.Zero)
}

//fun randomShape() = when(random.nextInt(1)) {
//    0 -> Circle
//    else -> Square
//}