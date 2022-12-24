import java.util.SortedSet
import kotlin.math.abs

fun main() {
    class Elf(val x: Int, val y: Int): Comparable<Elf> {
        fun propose(turn: Int, elves: SortedSet<Elf>): Elf {
            val nElf = Elf(x, y - 1)
            val sElf = Elf(x, y + 1)
            val eElf = Elf(x + 1, y)
            val wElf = Elf(x - 1, y)

            val n = nElf !in elves
            val ne = Elf(x + 1, y - 1) !in elves
            val e = eElf !in elves
            val se = Elf(x + 1, y + 1) !in elves
            val s = sElf !in elves
            val sw = Elf(x - 1, y + 1) !in elves
            val w = wElf !in elves
            val nw = Elf(x - 1, y - 1) !in elves

            if (n && ne && e && se && s && sw && w && nw) return this

            val movesNSWE = listOf(
                nElf.takeIf { nw && n && ne },
                sElf.takeIf { sw && s && se },
                wElf.takeIf { nw && w && sw },
                eElf.takeIf { ne && e && se },
            )
            val moves = movesNSWE.drop(turn % 4) + movesNSWE.take(turn % 4)
            return moves.filterNotNull().firstOrNull() ?: this
        }

        override fun compareTo(other: Elf): Int {
            val compY = y.compareTo(other.y)
            if (compY == 0)
                return x.compareTo(other.x)
            return compY
        }

        override fun toString(): String {
            return "($x, $y)"
        }
    }

    fun parseInput(input: List<String>): SortedSet<Elf> {
        val set = sortedSetOf<Elf>()
        input.forEachIndexed { y, line -> line.forEachIndexed { x, char -> if (char == '#') set.add(Elf(x, y)) } }
        return set
    }

    fun printMap(elves: SortedSet<Elf>) {
        val minX = elves.minOf { it.x }
        val minY = elves.minOf { it.y }
        val maxX = elves.maxOf { it.x }
        val maxY = elves.maxOf { it.y }

        (minY..maxY).forEach { y ->
            (minX..maxX).forEach { x ->
                print(if (Elf(x, y) in elves) '#' else '.')
            }
            println()
        }

        println("${elves.count()} elves in area $minX..$maxX : $minY..$maxY")
    }

    fun part1(input: List<String>): Int {
        var elves = parseInput(input)
        repeat(10) { turn ->
            printMap(elves)
            val elvesList = elves.toList() // For proper indexing
            val proposals = elvesList.map { it.propose(turn, elves) }
            val proposalsWithoutCollisions = proposals.filter { elf -> proposals.count { it.compareTo(elf) == 0 } == 1 }.toSortedSet()
            elves = proposals.mapIndexed { index, proposal -> if (proposal in proposalsWithoutCollisions) proposal else elvesList[index] }.toSortedSet()
        }

        println("(${elves.maxOf { it.x }} - ${elves.minOf { it.x }} + 1) * (${elves.maxOf { it.y }} - ${elves.minOf { it.y }} + 1) - ${elves.count()}")
        return (elves.maxOf { it.x } - elves.minOf { it.x } + 1) * (elves.maxOf { it.y } - elves.minOf { it.y } + 1) - elves.count()
    }

    fun part2(input: List<String>): Int {
        var elves = parseInput(input)
        var turn = 0
        var lastNumberOfMoves = 42

        while(lastNumberOfMoves > 0) {
            val elvesList = elves.toList() // For proper indexing
            val proposals = elvesList.map { it.propose(turn, elves) }
            val proposalsWithoutCollisions = proposals.filter { elf -> proposals.count { it.compareTo(elf) == 0 } == 1 }.toSortedSet()
            val newPositions = proposals.mapIndexed { index, proposal -> if (proposal in proposalsWithoutCollisions) proposal else elvesList[index] }.toSortedSet()

            lastNumberOfMoves = elves.zip(newPositions).count { it.first != it.second }
            println("Turn $turn : $lastNumberOfMoves moves")

            elves = newPositions
            ++turn
        }

        return turn
    }

    val input = readInput("Day23")
    println(part1(input))
    println(part2(input))
}
