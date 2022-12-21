fun main() {
    data class Number(val value: Long, val originalIndex: Int)

    fun mix(numbers: MutableList<Number>) {
        val n = numbers.size
        for (i in 0 until n) {
            val nextIndexToMove = numbers.indexOfFirst { it.originalIndex == i }
            val number = numbers.removeAt(nextIndexToMove)
            val newPosition = (((nextIndexToMove + number.value) % (n - 1)) + (n - 1)) % (n - 1)
            numbers.add(newPosition.toInt(), number)
        }
    }

    fun sumCoordinates(numbers: List<Number>): Long {
        val n = numbers.count()
        val startIndex = numbers.indexOfFirst { it.value == 0L }
        val after1000 = numbers[(startIndex + 1000) % n].value
        val after2000 = numbers[(startIndex + 2000) % n].value
        val after3000 = numbers[(startIndex + 3000) % n].value
        return after1000 + after2000 + after3000
    }

    fun part1(numbers: List<Int>): Long {
        val newList = numbers.mapIndexed { index, value -> Number(value.toLong(), index) }.toMutableList()
        mix(newList)
        return sumCoordinates(newList)
    }

    fun part2(numbers: List<Int>): Long {
        val newList = numbers.mapIndexed { index, value -> Number(value.toLong() * 811589153L, index) }.toMutableList()
        repeat(10) { mix(newList) }
        return sumCoordinates(newList)
    }

    val numbers = readInput("Day20").map { line -> line.toInt() }
//    println(part1(listOf(1, 2, -3, 3, -2, 0, 4)))
    println(part1(numbers))
    println(part2(numbers))
}
