import java.util.PriorityQueue
import kotlin.math.abs

fun main() {
    val windUp = 0b0001
    val windRight = 0b0010
    val windDown = 0b0100
    val windLeft = 0b1000

    class Map(val width: Int = 120, val height: Int = 25) {
        private val winds = Array(width * height) { 0 }

        fun addWind(x: Int, y: Int, direction: Int) {
            winds[y * width + x] += direction
        }

        fun nextMap(): Map {
            val map = Map(width, height)
            forEach { x, y, wind ->
                if (wind and windUp > 0) map.addWind(x, (y + height - 1) % height, windUp)
                if (wind and windRight > 0) map.addWind((x + 1) % width, y, windRight)
                if (wind and windDown > 0) map.addWind(x, (y + 1) % height, windDown)
                if (wind and windLeft > 0) map.addWind((x + width - 1) % width, y, windLeft)
            }
            return map
        }

        fun forEach(f: (x: Int, y: Int, wind: Int) -> Unit) {
            (0 until height).forEach { y ->
                (0 until width).forEach { x ->
                    f(x, y, winds[y * width + x])
                }
            }
        }

        fun isFree(x: Int, y: Int): Boolean {
            return x == 0 && y == -1 || x == 119 && y == 25 || x in 0..119 && y in 0..24 && winds[y * width + x] == 0
        }

        override fun toString(): String {
            val buffer = StringBuffer()
            forEach {x, y, wind ->
                if (x == 0 && y > 0) buffer.appendLine()
                when (wind) {
                    windUp -> buffer.append("^")
                    windRight -> buffer.append(">")
                    windDown -> buffer.append("v")
                    windLeft -> buffer.append("<")
                    0 -> buffer.append(".")
                    else -> buffer.append(
                        (if (wind and windUp > 0) 1 else 0) +
                                (if (wind and windRight > 0) 1 else 0) +
                                (if (wind and windDown > 0) 1 else 0) +
                                (if (wind and windLeft > 0) 1 else 0)
                    )
                }
            }
            return buffer.toString()
        }
    }

    class Position(val x: Int = 0, val y: Int = -1, val time: Int = 0) {
        fun nextPositions(nextMap: Map): List<Position> {
            return listOfNotNull(
                Position(x, y, time + 1).takeIf { nextMap.isFree(x, y) },
                Position(x + 1, y, time + 1).takeIf { nextMap.isFree(x + 1, y) },
                Position(x - 1, y, time + 1).takeIf { nextMap.isFree(x - 1, y) },
                Position(x, y - 1, time + 1).takeIf { nextMap.isFree(x, y - 1) },
                Position(x, y + 1, time + 1).takeIf { nextMap.isFree(x, y + 1) },
            )
        }

//        override fun compareTo(other: Position): Int {
//            return 2 * (other.y - y) + 10 * (other.x - x) + time - other.time
//        }
//
        override fun toString(): String {
            return "($x, $y, $time)"
        }

        override fun equals(other: Any?): Boolean {
            other as Position
            return other.x == x && other.y == y && other.time == time
        }

        override fun hashCode(): Int {
            return 120 * y + x + 3000 * time
        }
    }

    fun parseInput(input: List<String>): Map {
        val map = Map()
        input.drop(1).dropLast(1).forEachIndexed { y, line ->
            line.drop(1).dropLast(1).forEachIndexed { x, char ->
                map.addWind(x, y, when (char) { '^' -> windUp; '>' -> windRight; 'v' -> windDown; '<' -> windLeft; else -> 0 })
            }
        }
        return map
    }

    fun generateMaps(map: Map): List<Map> {
        val list = mutableListOf(map)
        repeat(599) {
            list.add(list.last().nextMap())
        }
        return list
    }

    class ForwardComparator: Comparator<Position> {
        override fun compare(o1: Position, o2: Position): Int {
            return 2 * (o2.y - o1.y) + 10 * (o2.x - o1.x) + o2.time - o1.time
        }
    }

    class BackwardComparator: Comparator<Position> {
        override fun compare(o1: Position, o2: Position): Int {
            return 2 * (o1.y - o2.y) + 10 * (o1.x - o2.x) + o2.time - o1.time
        }
    }

    fun findBest(maps: List<Map>, initialPosition: Position, goalX: Int, goalY: Int, comparator: Comparator<Position>): Int {
        val toExplore = PriorityQueue(comparator)
        toExplore.add(initialPosition)
        val explored = mutableListOf<Position>()
        var bestTiming = initialPosition.time + 1000
        while (toExplore.isNotEmpty()) {
            val position = toExplore.poll()
            if (position.x == goalX && position.y == goalY) {
                if (position.time < bestTiming) {
                    bestTiming = position.time
                    println("New best : $bestTiming")
                }
                continue
            }
            if (position.time + abs(goalX - position.x) + abs(goalY - position.y) >= bestTiming) {
                continue
            }
            if (position in explored) {
                continue
            }
            explored.add(position)
            position.nextPositions(maps[(position.time + 1) % 600]).forEach {
                if (it !in toExplore) toExplore.add(it)
            }
        }
        return bestTiming
    }

    fun part1(maps: List<Map>): Int {
        return findBest(maps, Position(), 119, 25, ForwardComparator())
    }

    fun part2(maps: List<Map>): Int {
        val position1 = Position()
        val position2 = Position(119, 25, findBest(maps, position1, 119, 25, ForwardComparator()))
        val position3 = Position(0, -1, findBest(maps, position2, 0, -1, BackwardComparator()))
        return findBest(maps, position3, 119, 25, ForwardComparator())
    }

    val input = readInput("Day24")
    val maps = generateMaps(parseInput(input))
    println(part1(maps))
    println(part2(maps))
}
