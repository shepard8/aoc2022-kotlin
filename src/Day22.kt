enum class Direction { Right, Down, Left, Up }
enum class CellType { OutsideMap, Ground, Wall }

fun main() {
    class Position(val row: Int, val column: Int, val direction: Direction): Comparable<Position> {
        fun next(): Position {
            return when (direction) {
                Direction.Right -> Position(row, column + 1, direction)
                Direction.Left -> Position(row, column - 1, direction)
                Direction.Up -> Position(row - 1, column, direction)
                Direction.Down -> Position(row + 1, column, direction)
            }
        }

        override fun compareTo(other: Position): Int {
            return (row - other.row) * 1000 + (column - other.column) * 4 + direction.ordinal - other.direction.ordinal
        }

        override fun toString(): String {
            return "($row, $column, $direction)"
        }
    }

    abstract class Map(inputLines: List<String>) {
        val cells: List<List<CellType>> = inputLines.map { line ->
            line.map { char ->
                when (char) {
                    '.' -> CellType.Ground
                    '#' -> CellType.Wall
                    else -> CellType.OutsideMap
                }
            }
        }

        fun nextTile(position: Position): Position {
            val trivialNext = position.next()
            if (cellTypeAt(trivialNext) == CellType.OutsideMap) {
                return warp(trivialNext)
            }
            return trivialNext
        }

        private fun cellTypeAt(position: Position): CellType {
            return cells.getOrNull(position.row)?.getOrNull(position.column) ?: CellType.OutsideMap
        }

        abstract fun warp(position: Position): Position

        fun isWall(position: Position): Boolean {
            return cellTypeAt(position) == CellType.Wall
        }

        fun startingPosition(): Position {
            return Position(0, cells[0].indexOfFirst { it != CellType.OutsideMap }, Direction.Right)
        }
    }

    class Step1Map(inputLines: List<String>): Map(inputLines) {
        override fun warp(position: Position): Position {
            return when (position.direction) {
                Direction.Right -> Position(position.row, cells[position.row].indexOfFirst { it != CellType.OutsideMap }, position.direction)
                Direction.Left -> Position(position.row, cells[position.row].indexOfLast { it != CellType.OutsideMap }, position.direction)
                Direction.Down -> Position(cells.indexOfFirst { (it.getOrNull(position.column) ?: CellType.OutsideMap) != CellType.OutsideMap }, position.column, position.direction)
                Direction.Up -> Position(cells.indexOfLast { (it.getOrNull(position.column) ?: CellType.OutsideMap) != CellType.OutsideMap }, position.column, position.direction)
            }
        }
    }

    class Step2Map(inputLines: List<String>): Map(inputLines) {
        val frontiers = sortedMapOf<Position, Position>()

        init {
            connect(makeFrontier(0, 2, Direction.Right), makeFrontier(2, 1, Direction.Right), true)
            connect(makeFrontier(0, 2, Direction.Down), makeFrontier(1, 1, Direction.Right))
            connect(makeFrontier(2, 1, Direction.Down), makeFrontier(3, 0, Direction.Right))
            connect(makeFrontier(3, 0, Direction.Down), makeFrontier(0, 2, Direction.Up))
            connect(makeFrontier(3, 0, Direction.Left), makeFrontier(0, 1, Direction.Up))
            connect(makeFrontier(2, 0, Direction.Left), makeFrontier(0, 1, Direction.Left), true)
            connect(makeFrontier(2, 0, Direction.Up), makeFrontier(1, 1, Direction.Left))
        }

        private fun connect(from: List<Position>, to: List<Position>, reversed: Boolean = false) {
            from.zip(if (reversed) to.reversed() else to).forEach {
//                println("${it.first} <-> ${it.second}")
                frontiers[it.first] = it.second
                frontiers[it.second] = it.first
            }
        }

        private fun makeFrontier(squareRow: Int, squareColumn: Int, direction: Direction): List<Position> {
            val startRow = squareRow * 50 + when (direction) {
                Direction.Right -> 0
                Direction.Left -> 0
                Direction.Up -> -1
                Direction.Down -> 50
            }
            val startColumn = squareColumn * 50 + when (direction) {
                Direction.Right -> 50
                Direction.Left -> -1
                Direction.Up -> 0
                Direction.Down -> 0
            }
            val deltaRow = if (direction in listOf(Direction.Left, Direction.Right)) 1 else 0
            val deltaColumn = 1 - deltaRow
            return (0 until 50).map {
                Position(startRow + it * deltaRow, startColumn + it * deltaColumn, direction)
            }
        }

        override fun warp(position: Position): Position {
            val nextOppositePosition = frontiers[position]!!
            val nextPosition = Position(nextOppositePosition.row, nextOppositePosition.column, Direction.values()[(nextOppositePosition.direction.ordinal + 2) % 4]).next()
            println("Warping at $position --> $nextPosition.")
            return nextPosition
        }
    }

    class Player(val map: Map) {
        var position: Position = map.startingPosition()

        fun turnLeft() {
            position = Position(position.row, position.column, Direction.values()[(position.direction.ordinal + 3) % 4])
        }

        fun turnRight() {
            position = Position(position.row, position.column, Direction.values()[(position.direction.ordinal + 1) % 4])
        }

        fun moveForward(n: Int) {
            for (i in 0 until n) {
                if (!moveForward()) {
                    break
                }
            }
        }

        fun moveForward(): Boolean {
            val nextPosition = map.nextTile(position)
            if (map.isWall(nextPosition)) {
                return false
            }
            position = nextPosition
            return true
        }

        override fun toString(): String {
            return "Player at (${position.row}, ${position.column}), facing ${position.direction}"
        }
    }

    fun computeAnswer(player: Player, movesInput: String): Int {
        val moves = movesInput.split(Regex("((?=L)|(?<=L)|(?=R)|(?<=R))"))
        moves.forEach {
            println("${player.position} / $it")
            when (it) {
                "L" -> player.turnLeft()
                "R" -> player.turnRight()
                else -> player.moveForward(it.toInt())
            }
        }

        return 1000 * (player.position.row + 1) + 4 * (player.position.column + 1) + player.position.direction.ordinal
    }

    fun part1(input: List<String>): Int {
        val mapInput = input.takeWhile { it.isNotEmpty() }
        val movesInput = input.last()

        val map = Step1Map(mapInput)
        val player = Player(map)

        return computeAnswer(player, movesInput)
    }

    fun part2(input: List<String>): Int {
        val mapInput = input.takeWhile { it.isNotEmpty() }
        val movesInput = input.last()

        val map = Step2Map(mapInput)
        val player = Player(map)

        return computeAnswer(player, movesInput)
//        return 42
    }

    val input = readInput("Day22")
    println(part1(input))
    println(part2(input))
}
