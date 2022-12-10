class Processor {
    private val values = mutableListOf(1)

    fun noop() {
        printPixel(values.count() - 1, values.last())

        values.add(values.last())
    }

    fun addX(increment: Int) {
        printPixel(values.count() - 1, values.last())
        printPixel(values.count(), values.last())

        values.add(values.last())
        values.add(values.last() + increment)
    }

    fun strengthAt(index: Int): Int {
        return index * values[index]
    }

    private fun printPixel(cycle: Int, value: Int) {
        if (cycle % 40 == 0) {
            println()
        }
        else if (cycle % 5 == 0) {
            print(' ')
        }
        if (cycle%40 in (value-1..value+1)) {
            print('#')
        }
        else {
            print('.')
        }
    }
}

fun main() {
    fun part1(processor: Processor): Int {
        return (0..5).map { 40 * it + 20 }.sumOf { processor.strengthAt(it) }
    }

    val input = readInput("Day10")
    val processor = Processor()
    input.forEach { line ->
        if (line.startsWith("noop")) {
            processor.noop()
        }
        else {
            processor.addX(line.split(" ")[1].toInt())
        }
    }

    println()
    println(part1(processor))
}
