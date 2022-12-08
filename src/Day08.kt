import kotlin.math.min

fun main() {
    fun part1(input: List<List<Char>>): Int {
        var visible = 99 + 99 + 97 + 97
        (1..97).forEach { y ->
            (1..97).forEach { x ->
                val cell = input[y][x]
                val visibleFromLeft = cell > input[y].take(x).max()
                val visibleFromRight = cell > input[y].takeLast(99 - x - 1).max()
                val visibleFromTop = cell > input.take(y).maxOf { it[x] }
                val visibleFromBottom = cell > input.takeLast(99 - y - 1).maxOf { it[x] }
                if (visibleFromLeft || visibleFromRight || visibleFromTop || visibleFromBottom) {
                    visible += 1
                }
            }
        }
        return visible
    }

    fun part2(input: List<List<Char>>): Int {
        var best = 4
        (0..98).forEach { y ->
            (0..98).forEach { x ->
                val cell = input[y][x]
                val visibleOnLeft = min(x, input[y].take(x).takeLastWhile { it < cell }.count() + 1)
                val visibleOnRight = min(99 - x - 1, input[y].takeLast(99 - x - 1).takeWhile { it < cell }.count() + 1)
                val visibleOnTop = min(y, input.take(y).map { it[x] }.takeLastWhile { it < cell }.count() + 1)
                val visibleOnBottom = min(99 - y - 1, input.takeLast(99 - y - 1).map { it[x] }.takeWhile { it < cell }.count() + 1)
                val scenicScore = visibleOnLeft * visibleOnRight * visibleOnTop * visibleOnBottom
                if (scenicScore > best) {
//                    println("($x, $y) : $visibleOnLeft * $visibleOnRight * $visibleOnTop * $visibleOnBottom = $scenicScore")
                    best = scenicScore
                }
            }
        }
        return best
    }

    val input = readInput("Day08").map { it.toList() }

    println(part1(input))
    println(part2(input))
}
