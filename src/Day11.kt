import java.util.*

class Monkey(
    items: List<Long>,
    private val op: (Long) -> Long,
    private val divisor: Long,
    private val monkeyTrue: Int,
    private val monkeyFalse: Int
) {
    companion object {
        fun round(monkeys: List<Monkey>, easyMode: Boolean) {
            monkeys.forEach {
                it.turn(monkeys, easyMode)
            }
        }
    }

    private val itemsQueue: Queue<Long> = LinkedList(items)
    private var inspected = 0

    fun countInspected(): Int {
        return inspected
    }

    private fun receive(item: Long) {
        itemsQueue.add(item)
    }

    private fun turn(monkeys: List<Monkey>, easyMode: Boolean) {
        while (itemsQueue.isNotEmpty()) {
            inspect(itemsQueue.remove(), monkeys, easyMode)
        }
    }

    private fun inspect(item: Long, monkeys: List<Monkey>, easyMode: Boolean) {
        val worry = if (easyMode) {
            op(item) / 3
        }
        else {
            op(item) % (7 * 11 * 13 * 3 * 17 * 2 * 5 * 19)
        }
        if (worry % divisor == 0.toLong()) {
            monkeys[monkeyTrue].receive(worry)
        }
        else {
            monkeys[monkeyFalse].receive(worry)
        }

        ++inspected
    }
}

fun main() {
    fun part1(monkeys: List<Monkey>) {
        repeat(20) {
            Monkey.round(monkeys, true)
        }
        println(monkeys.map { it.countInspected() }.sorted().takeLast(2).reduce { a, b -> a * b })
    }

    fun part2(monkeys: List<Monkey>) {
        repeat(10000) {
            if (it % 100 == 0) {
                println(it)
            }
            Monkey.round(monkeys, false)
        }
        monkeys.map { it.countInspected() }.sorted().takeLast(2).forEach { println(it) }
        println(monkeys.map { it.countInspected().toLong() }.sorted().takeLast(2).reduce { a, b -> a * b })
    }

    fun createList(): List<Monkey> {
        return listOf(
            Monkey(listOf(63, 57), { it * 11 }, 7, 6, 2),
            Monkey(listOf(82, 66, 87, 78, 77, 92, 83), { it + 1 }, 11, 5, 0),
            Monkey(listOf(97, 53, 53, 85, 58, 54), { it * 7 }, 13, 4, 3),
            Monkey(listOf(50), { it + 3 }, 3, 1, 7),
            Monkey(listOf(64, 69, 52, 65, 73), { it + 6 }, 17, 3, 7),
            Monkey(listOf(57, 91, 65), { it + 5 }, 2, 0, 6),
            Monkey(listOf(67, 91, 84, 78, 60, 69, 99, 83), { it * it }, 5, 2, 4),
            Monkey(listOf(58, 78, 69, 65), { it + 7 }, 19, 5, 1),
        )
    }

    println(part1(createList()))
    println(part2(createList()))
}
