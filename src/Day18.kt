import kotlin.math.abs
import kotlin.math.max

fun main() {
    open class Lava(val x: Int, val y: Int, val z: Int): Comparable<Lava> {
        override fun toString(): String {
            return "Lava($x, $y, $z)"
        }

        fun touches(other: Lava): Boolean {
            val dx = abs(x - other.x)
            val dy = abs(y - other.y)
            val dz = abs(z - other.z)
            return dx + dy + dz == 1
        }

        override fun compareTo(other: Lava): Int {
            val dx = x - other.x
            val dy = y - other.y
            val dz = z - other.z
            return 4 * dx / max(1, abs(dx)) + 2 * dy / max(1, abs(dy)) + dz / max(1, abs(dz))
        }
    }

    class Air(x: Int, y: Int, z: Int): Lava(x, y, z) {
        fun adjacent(): List<Air> {
            return listOf(
                Air(x - 1, y, z),
                Air(x + 1, y, z),
                Air(x, y - 1, z),
                Air(x, y + 1, z),
                Air(x, y, z - 1),
                Air(x, y, z + 1),
            )
        }

        override fun toString(): String {
            return "Air($x, $y, $z)"
        }
    }

    fun part1(lava: List<Lava>): Int {
        return 6 * lava.count() - lava.sumOf { cube1 ->
            lava.count { cube2 -> cube1.touches(cube2) }
        }
    }

    fun part2(lava: List<Lava>): Int {
        // x range: 0 - 21, y range: 0 - 21, z range : 0 - 20
        // Start from (-1, -1, -1); expand as much as possible; count surface
        val toExplore = sortedSetOf(Air(-1, -1, -1))
        val explored = lava.toSortedSet()
        while (toExplore.isNotEmpty()) {
            val air = toExplore.pollFirst()!!
            explored.add(air)
            val next = air.adjacent().filter { it.x >= -1 && it.x <= 22 && it.y >= -1 && it.y <= 22 && it.z >= -1 && it.z <= 21 && it !in toExplore && it !in explored }
            toExplore.addAll(next)
        }

        return explored.filterIsInstance<Air>().sumOf { air ->
            lava.count { cube -> air.touches(cube) }
        }
    }

    val input = readInput("Day18").map { line ->
        val (x, y, z) = line.split(",")
        Lava(x.toInt(), y.toInt(), z.toInt())
    }
    println(part1(input))
    println(part2(input))
}
