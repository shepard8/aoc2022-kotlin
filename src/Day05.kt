data class Move(val count: Int, val fromStack: Int, val toStack: Int)

val stacks = listOf(
    listOf(),
    listOf('D', 'H', 'R', 'Z', 'S', 'P', 'W', 'Q'),
    listOf('F', 'H', 'Q', 'W', 'R', 'B', 'V'),
    listOf('H', 'S', 'V', 'C'),
    listOf('G', 'F', 'H'),
    listOf('Z', 'B', 'J', 'G', 'P'),
    listOf('L', 'F', 'W', 'H', 'J', 'T', 'Q'),
    listOf('N', 'J', 'V', 'L', 'D', 'W', 'T', 'Z'),
    listOf('F', 'H', 'G', 'J', 'C', 'Z', 'T', 'D'),
    listOf('H', 'B', 'M', 'V', 'P', 'W'),
)

fun main() {
    fun part1(moves: List<Move>): String {
        val stacks = stacks.toMutableList()
        moves.forEach { move ->
            val items = stacks[move.fromStack].take(move.count)
            stacks[move.fromStack] = stacks[move.fromStack].drop(move.count)
            stacks[move.toStack] = items.reversed() + stacks[move.toStack]
        }
        return stacks.drop(1).map { it.first() }.joinToString("")
    }

    fun part2(moves: List<Move>): String {
        val stacks = stacks.toMutableList()
        moves.forEach { move ->
            val items = stacks[move.fromStack].take(move.count)
            stacks[move.fromStack] = stacks[move.fromStack].drop(move.count)
            stacks[move.toStack] = items + stacks[move.toStack]
        }
        return stacks.drop(1).map { it.first() }.joinToString("")
    }

    val input = readInput("Day05").drop(10)
    val moves = sequence {
        input.forEach { line ->
            val ints = line.split(" ")
            yield(Move(ints[1].toInt(), ints[3].toInt(), ints[5].toInt()))
        }
    }.toList()

    println(part1(moves))
    println(part2(moves))
}
