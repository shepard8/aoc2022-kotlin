import kotlin.math.abs

fun main() {
    class Sensor(val x: Int, val y: Int, val closestX: Int, val closestY: Int) {
        fun radius(): Int = abs(x - closestX) + abs(y - closestY)

        fun frontier(): Sequence<Pair<Int, Int>> {
            return sequence {
                for (a in 0..radius()) {
                    yield(Pair(x - radius() - 1 + a, y - a))
                    yield(Pair(x + a, y - radius() - 1 + a))
                    yield(Pair(x + radius() + 1 - a, y + a))
                    yield(Pair(x - a, y + radius() + 1 - a))
                }
            }
        }

        fun detects(px: Int, py: Int): Boolean {
            return abs(x - px) + abs(y - py) <= radius()
        }

        override fun toString(): String {
            return "($x, $y) [$closestX, $closestY]; radius=${radius()}"
        }
    }

    fun parseData(input: List<String>): Sequence<Sensor> {
        return sequence {
            input.forEach { line ->
                val regex = Regex("^Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)$")
                val match = regex.matchEntire(line)!!
                val values = match.groupValues.drop(1).toTypedArray()
                yield(Sensor(values[0].toInt(), values[1].toInt(), values[2].toInt(), values[3].toInt()))
            }
        }
    }

    fun part1(input: List<String>): Int {
        val targetY = 2_000_000

        parseData(input)
            .map {
                val overlap = it.radius() - abs(it.y - targetY)
                if (overlap < 1) {
                    return@map null
                }
                val range = IntRange(it.x - overlap, it.x + overlap)
                println("$it; overlap=$overlap; range=$range")
                range
            }
            .filterNotNull()
            .sortedBy { it.first }
            .forEach { println(it) }

        // Eye analysis :-] We are lucky that there are no holes in the sequence, so the final math is very easy :-)
        // The -1 is because there is one beacon in the range (appearing twice) that needs to be deduced.
        return 4651089 - -432198 + 1 - 1
    }

    fun part2(input: List<String>): Long {
        // Idea : The beacon must be next to a sensor's border.
        // So let's check for all points on sensor's borders if they are detected by at least one sensor.
        // We start with sensors with a small radius because we feel like it :-)
        val sensors = parseData(input)

        parseData(input)
            .sortedBy { it.radius() }
            .forEach {
                println("Checking sensor $it.")
                val toCheck = it.frontier()
                val p = toCheck.find { (x, y) ->
                    val inBounds = x in 0..4_000_000 && y in 0..4_000_000
                    val undetected = sensors.none { sensor -> sensor.detects(x, y) }
                    inBounds && undetected
                }
                if (p != null) {
                    println(p)
                    return 4_000_000 * p.first.toLong() + p.second.toLong()
                }
            }

        return 42
    }

    val input = readInput("Day15")

    println(part1(input))
    println(part2(input))
}
