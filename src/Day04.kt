fun main() {
    fun part1(pairs: List<Pair<IntRange, IntRange>>) = pairs.count { (range1, range2) ->
        range1.contains(range2.first) && range1.contains(range2.last) || range2.contains(range1.first) && range2.contains(range1.last)
    }

    fun part2(pairs: List<Pair<IntRange, IntRange>>) = pairs.count { (range1, range2) ->
        range1.contains(range2.first) || range2.contains(range1.first)
    }

    val input = readInput("Day04")
    val pairs = sequence {
        input.forEach { line ->
            val ints = line.split(",", "-").map { it.toInt() }
            yield(Pair(IntRange(ints[0], ints[1]), IntRange(ints[2], ints[3])))
        }
    }.toList()

    println(part1(pairs))
    println(part2(pairs))
}
