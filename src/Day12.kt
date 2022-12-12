fun main() {
    fun part1(heights: List<List<Char>>) {
        val chars = listOf(' ','╶','╷','┌','╴','─','┐','┬','╵','└','│','├','┘','┴','┤','┼')
        heights.forEachIndexed { y, line -> line.forEachIndexed { x, c ->
            val leftAllowed = heights[y].getOrElse(x - 1) { 'z' + 2 } <= c + 1
            val rightAllowed = heights[y].getOrElse(x + 1) { 'z' + 2 } <= c + 1
            val upAllowed = (heights.getOrElse(y - 1) { listOf() }).getOrElse(x) { 'z' + 2 } <= c + 1
            val downAllowed = (heights.getOrElse(y + 1) { listOf() }).getOrElse(x) { 'z' + 2 } <= c + 1

            if (x == 0) {
                println()
            }
            val char =
                (if (upAllowed) 8 else 0) +
                (if (leftAllowed) 4 else 0) +
                (if (downAllowed) 2 else 0) +
                (if (rightAllowed) 1 else 0)
            print(chars[char])
        } }
    }

    val input = readInput("Day12")

    println()
    println(part1(input.map { it.toList() }))
}
