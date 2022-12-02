fun main() {
    fun part1(games: List<Pair<Int, Int>>) =
        games.sumOf { (opponent, me) ->
            val shapeScore = me + 1
            val gameScore = if (me - opponent == 1 || me - opponent == -2) 6 else if (me == opponent) 3 else 0
            shapeScore + gameScore
        }

    fun part2(games: List<Pair<Int, Int>>) =
        games.sumOf { (opponent, outcome) ->
            val me = (opponent + 2 + outcome) % 3
            val shapeScore = me + 1
            val gameScore = 3 * outcome
            shapeScore + gameScore
        }

    val input = readInput("Day02")
    val games = sequence {
        input.forEach { line ->
            val opponent = line[0] - 'A'
            val me = line[2] - 'X'
            yield(Pair(opponent, me))
        }
    }.toList()

    println(part1(games))
    println(part2(games))
}
