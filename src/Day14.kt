open class Point(val x: Int, val y: Int): Comparable<Point> {
    open val char = '~'

    override fun toString(): String {
        return "($x, $y)"
    }

    override fun compareTo(other: Point): Int {
        return if (x < other.x || x == other.x && y < other.y) {
            -1
        } else if (x == other.x && y == other.y) {
            0
        }
        else {
            1
        }
    }
}

class Wall(x: Int, y: Int): Point(x, y) {
    override val char = '#'

    constructor(s: String): this(s.split(",")[0].toInt(), s.split(",")[1].toInt())

    fun pointsBetween(other: Point): List<Point> {
        return if (x == other.x) {
            IntRange(minOf(y, other.y), maxOf(y, other.y)).map { Wall(x, it) }
        }
        else {
            IntRange(minOf(x, other.x), maxOf(x, other.x)).map { Wall(it, y) }
        }
    }
}
class Sand(x: Int, y: Int): Point(x, y) {
    override val char = 'o'
}

object Origin: Point(500, 0) {
    override val char = '+'
}

fun main() {
    fun print(obstacles: MutableSet<Point>) {
        for (x in obstacles.minOf { it.x }..obstacles.maxOf { it.x }) {
            print((x / 100) % 10)
        }
        println()
        for (x in obstacles.minOf { it.x }..obstacles.maxOf { it.x }) {
            print((x / 10) % 10)
        }
        println()
        for (x in obstacles.minOf { it.x }..obstacles.maxOf { it.x }) {
            print(x % 10)
        }
        println()
        for (y in obstacles.minOf { it.y }..obstacles.maxOf { it.y }) {
            for (x in obstacles.minOf { it.x }..obstacles.maxOf { it.x }) {
                val char = obstacles.find { it.x == x && it.y == y}?.char ?: '.'
                print(char)
            }
            println(y)
        }
    }

    fun drop(curPos: Point, obstacles: MutableSet<Point>, trace: Boolean): Boolean {
        if (curPos.y > obstacles.filterIsInstance<Wall>().maxOf { it.y }) {
            return false
        }

        val below = Point(curPos.x, curPos.y + 1)
        if (below !in obstacles) {
            if (trace) obstacles.add(below)
            return drop(below, obstacles, trace)
        }

        val belowLeft = Point(curPos.x - 1, curPos.y + 1)
        if (belowLeft !in obstacles) {
            if (trace) obstacles.add(belowLeft)
            return drop(belowLeft, obstacles, trace)
        }

        val belowRight = Point(curPos.x + 1, curPos.y + 1)
        if (belowRight !in obstacles) {
            if (trace) obstacles.add(belowRight)
            return drop(belowRight, obstacles, trace)
        }

        obstacles.add(Sand(curPos.x, curPos.y))
        return true
    }

    fun genWalls(input: List<String>): MutableSet<Point> {
        val walls = sortedSetOf<Point>()
        input.forEach { line ->
            val stops = line.split(" -> ")
            var curPoint = Wall(stops[0])
            walls.add(curPoint)
            line.split(" -> ").forEach {
                val nextPoint = Wall(it)
                walls.addAll(curPoint.pointsBetween(nextPoint))
                curPoint = nextPoint
            }
        }
        println(walls.joinToString(", ", "[", "]"))
        println("Min : (${walls.minOf { it.x }}, ${walls.minOf { it.y }})")
        println("Max : (${walls.maxOf { it.x }}, ${walls.maxOf { it.y }})")

        return walls
    }

    fun showNthSand(walls: MutableSet<Point>, n: Int) {
        repeat(n) { drop(Origin, walls, false) }
        drop(Origin, walls, true)
        print(walls)
    }

    fun part1(input: List<String>): Int {
        val walls = genWalls(input)

        var i = 0
        while (drop(Origin, walls, false)) {
            ++i
        }

        return i
    }

    fun part2(input: List<String>): Int {
        val walls = genWalls(input)
        walls.addAll(Wall(300, 163).pointsBetween(Wall(700, 163)))

        var i = 0
        while (Sand(500, 0) !in walls) {
            if (i > 0 && i % 1000 == 0) {
                print("...")
                println(i)
            }

            drop(Origin, walls, false)
            ++i
        }
        return i
    }

    val input = readInput("Day14")

    showNthSand(genWalls(input), 874)
    println(part1(input))
    println(part2(input))
}
