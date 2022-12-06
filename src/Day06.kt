fun main() {
    fun part1(input: String): Int {
        var cur = input.take(4)
        var i = 4
        while (cur.toList().distinct().count() < 4) {
            cur = cur.drop(1) + input[i++]
        }
        return i
    }

    fun part2(input: String): Int {
        var cur = input.take(14)
        var i = 14
        while (cur.toList().distinct().count() < 14) {
            cur = cur.drop(1) + input[i++]
        }
        return i
    }

    val input = readInput("Day06").first()

    println(part1(input))
    println(part2(input))
}
