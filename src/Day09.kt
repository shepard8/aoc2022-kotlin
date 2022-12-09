import kotlin.math.abs

open class Node {
    protected var x = 0
    protected var y = 0

    fun move(dx: Int, dy: Int) {
        x += dx
        y += dy
    }

    open fun follow(other: Node) {
        val dx = abs(other.x - x)
        val dy = abs(other.y - y)
        if (dx > 1 && dy > 1) {
            x = (x + other.x) / 2
            y = (y + other.y) / 2
        }
        else if (dx > 1) {
            x = (x + other.x) / 2
            y = other.y
        }
        else if (dy > 1) {
            y = (y + other.y) / 2
            x = other.x
        }
    }
}

class CountingNode: Node() {
    private val visitedPositions = mutableSetOf(Pair(0, 0))

    override fun follow(other: Node) {
        super.follow(other)
        visitedPositions.add(Pair(x, y))
    }

    fun numberOfVisitedPositions() = visitedPositions.count()
}

fun unitVector(direction: String): Pair<Int, Int> = when (direction) {
    "U" -> Pair(0, 1)
    "D" -> Pair(0, -1)
    "L" -> Pair(-1, 0)
    "R" -> Pair(1, 0)
    else -> throw IllegalArgumentException()
}

fun part1(input: List<String>): Int {
    val head = Node()
    val tail = CountingNode()

    input.forEach { line ->
        val (direction, count) = line.split(" ")
        val (dx, dy) = unitVector(direction)
        for (i in 1..count.toInt()) {
            head.move(dx, dy)
            tail.follow(head)
        }
    }

    return tail.numberOfVisitedPositions()
}

fun part2(input: List<String>): Int {
    val nodes = (1..9).map { Node() } + CountingNode()

    input.forEach { line ->
        val (direction, count) = line.split(" ")
        val (dx, dy) = unitVector(direction)
        for (i in 1..count.toInt()) {
            nodes[0].move(dx, dy)
            (1..9).forEach {
                nodes[it].follow(nodes[it - 1])
            }
        }
    }

    return nodes.filterIsInstance<CountingNode>().first().numberOfVisitedPositions()
}

fun main() {
    val input = readInput("Day09")

    println(part1(input))
    println(part2(input))
}
