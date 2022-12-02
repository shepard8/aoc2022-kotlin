fun main() {
    fun part1(sums: List<Int>) = sums.max()

    fun part2(sums: List<Int>) = sums.sortedDescending().take(3).sum()

    val input = readInput("Day01")
    val sums = sequence {
        var curSum = 0

        input.forEach { line ->
            if (line.isBlank()) {
                yield(curSum)
                curSum = 0
            }
            else {
                curSum += line.toInt()
            }
        }

        yield(curSum)
    }.toList()

    println(part1(sums))
    println(part2(sums))
}
