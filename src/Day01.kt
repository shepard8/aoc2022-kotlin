fun main() {
    fun part1(input: List<Int>): Int {
        return input.max()
    }

    fun part2(input: List<Int>): Int {
        return input.sortedDescending().take(3).sum()
    }

    val input = readInput("Day01")
    val sums = sequence {
        var curSum = 0

        input.forEach { line ->
            if (line.isNullOrBlank()) {
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
