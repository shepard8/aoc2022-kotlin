fun main() {
    class Rock(val x: Long, val y: Long) {
        override fun toString(): String {
            return "($x, $y)"
        }
    }

    class Map {
        private val rocks = mutableListOf<Rock>()
        private var highest = 0L

        fun add(block: List<Rock>) {
            rocks.addAll(block)
            highest = maxOf(highest, block.maxOf { it.y })
        }

        fun highest(): Long {
            return highest
        }

        fun canMoveDown(block: List<Rock>): Boolean {
            // Possible optimization : filter block to keep only bottom rocks
            return block.all { blockRock ->
                blockRock.y > 1 && !rocks.any { mapRock ->
                    mapRock.x == blockRock.x && mapRock.y + 1 == blockRock.y
                }
            }
        }

        fun canMoveLeft(block: List<Rock>): Boolean {
            // Possible optimization : filter block to keep only left rocks
            return block.all { blockRock ->
                blockRock.x > 0L && !rocks.any { mapRock ->
                    mapRock.y == blockRock.y && mapRock.x + 1 == blockRock.x
                }
            }
        }

        fun canMoveRight(block: List<Rock>): Boolean {
            // Possible optimization : filter block to keep only right rocks
            return block.all { blockRock ->
                blockRock.x < 6L && !rocks.any { mapRock ->
                    mapRock.y == blockRock.y && mapRock.x == blockRock.x + 1
                }
            }
        }

        override fun toString(): String {
            // Show the top of the map
            return rocks.filter { it.y > highest - 30 }.joinToString(", ", "[", "]")
        }
    }

    class Block(val rocks: List<Rock>, private val map: Map) {
        constructor(map: Map, vararg parts: Rock): this(parts.toList(), map)

        fun spawn(): Block {
            return Block(rocks.map { Rock(it.x, it.y + map.highest() + 4L) }, map)
        }

        fun moveLeft(): Block {
            if (map.canMoveLeft(rocks)) {
                return Block(rocks.map { Rock(it.x - 1L, it.y) }, map)
            }
            return this
        }

        fun moveRight(): Block {
            if (map.canMoveRight(rocks)) {
                return Block(rocks.map { Rock(it.x + 1L, it.y) }, map)
            }
            return this
        }

        fun moveDown(): Block? {
            if (map.canMoveDown(rocks)) {
                return Block(rocks.map { Rock(it.x, it.y - 1L) }, map)
            }
            map.add(this.rocks)
            return null
        }

        override fun toString(): String = rocks.joinToString(", ", "[", "]")
    }

    fun initBlocks(map: Map) = listOf(
        Block(map, Rock(2, 0), Rock(3, 0), Rock(4, 0), Rock(5, 0)),
        Block(map, Rock(3, 0), Rock(2, 1), Rock(3, 1), Rock(4, 1), Rock(3, 2)),
        Block(map, Rock(2, 0), Rock(3, 0), Rock(4, 0), Rock(4, 1), Rock(4, 2)),
        Block(map, Rock(2, 0), Rock(2, 1), Rock(2, 2), Rock(2, 3)),
        Block(map, Rock(2, 0), Rock(3, 0), Rock(2, 1), Rock(3, 1)),
    )

    fun part1(input: String): Long {
        val map = Map()
        val initBlocks = initBlocks(map)
        var i = 0
        val printUntil = 0

        var block: Block?
        repeat(2022) {
            block = initBlocks[it % 5].spawn()
            if (it < printUntil) println("init: $block")
            while (block != null) {
                if (input[i++ % input.length] == '<') {
                    block = block!!.moveLeft()
                    if (it < printUntil) println("left: $block")
                }
                else {
                    block = block!!.moveRight()
                    if (it < printUntil) println("right: $block")
                }
                block = block!!.moveDown()
                if (it < printUntil) println("down: $block")
            }
            if (it < printUntil) println()
        }

        return map.highest()
    }

    fun part2(input: String): Long {
        // After any multiple of 10091 steps, the situation is the same with an additional height of 2610.
        // So we let the simulation run for 10091 steps, then we fast-forward to the last iteration of 10091 steps before reaching 1_000_000_000_000 steps.

        // Drop 1704 blocks (to reach blow 10091)
        // There remain 999999998296 blocks to drop. Dropping 1695 blocks is the same as performing 10091 blows.
        // We skip 1695 * (999999998296 / 1695) blocks, of height 2610 * (999999998296 / 1695).
        // We drop the remaining 999999998296 % 1695 blocks.
        // Total of blocks to drop = 1704 + (999999998296 % 1695) = 2500
        // Total height is map height + 2610 * (999999998296 / 1695)

        val map = Map()
        val initBlocks = initBlocks(map)

        var blowId = 0
        var blockId = 0

        var block: Block?
        repeat(2500) {
            block = initBlocks[blockId++ % 5].spawn()
            while (block != null) {// && blowId < 10091) {
                if (input[blowId++ % input.length] == '<') {
                    block = block!!.moveLeft()
                }
                else {
                    block = block!!.moveRight()
                }
                block = block!!.moveDown()
            }
        }

        return map.highest() + 2610L * (999999998296L / 1695L)
    }

    val input = readInput("Day17")[0]
    println(part1(input))
    println(part2(input))
}
