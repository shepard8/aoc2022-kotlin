fun main() {
    fun priority(item: Char) = when(item) {
        in 'a'..'z' -> item - 'a' + 1
        in 'A'..'Z' -> item - 'A' + 27
        else -> throw IllegalArgumentException()
    }

    fun part1(rucksacks: List<String>) =
        rucksacks.map { line ->
            val left = line.subSequence(0, line.length / 2)
            val right = line.subSequence(line.length / 2, line.length)
            left.first { c -> c in right }
        }.sumOf { priority(it) }

    fun part2(rucksacks: List<String>) =
        rucksacks.chunked(3).map { group ->
            group[0].first { c -> c in group[1] && c in group[2] }
        }.sumOf { priority(it) }

    val input = readInput("Day03")

    println(part1(input))
    println(part2(input))
}
