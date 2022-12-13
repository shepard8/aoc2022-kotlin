sealed interface Tree13: Comparable<Tree13>

class Node13(private vararg val subtrees: Tree13): Tree13, List<Tree13> by subtrees.toList() {
    override fun compareTo(other: Tree13): Int {
        return when(other) {
            is Leaf13 -> this.compareTo(Node13(other))
            is Node13 -> compareToNode13(other)
        }
    }

    private fun compareToNode13(other: Node13): Int {
        return when {
            this.isEmpty() && other.isEmpty() -> 0
            this.isEmpty() && other.isNotEmpty() -> -1
            this.isNotEmpty() && other.isEmpty() -> 1
            else -> {
                this.first().compareTo(other.first()).takeUnless { it == 0 } ?:
                Node13(*this.drop(1).toTypedArray()).compareTo(Node13(*other.drop(1).toTypedArray()))
            }
        }
    }
}

class Leaf13(val number: Int): Tree13 {
    override fun compareTo(other: Tree13): Int {
        return when(other) {
            is Leaf13 -> this.number.compareTo(other.number)
            is Node13 -> Node13(this).compareTo(other)
        }
    }
}

fun main() {
    fun part1(signals: List<Pair<Tree13, Tree13>>): Int {
        return signals.mapIndexedNotNull { index, (signal1, signal2) -> (index + 1).takeIf { signal1 < signal2 } }.sum()
    }

    fun part2(signals: List<Tree13>): Int {
        val sep1 = Node13(Node13(Leaf13(2)))
        val sep2 = Node13(Node13(Leaf13(6)))
        val all = signals + listOf(sep1, sep2)
        val sorted = all.sorted()
        val index1 = sorted.indexOf(sep1) + 1
        val index2 = sorted.indexOf(sep2) + 1
        return index1 * index2
    }

    fun stringToTree(input: MutableList<Char>): Tree13 {
        fun readElement(): Tree13 {
            if (input[0] == ',') {
                input.removeAt(0)
            }
            return if (input[0] == '[') {
                input.removeAt(0)
                val elements = sequence {
                    while (input[0] != ']') {
                        yield(readElement())
                    }
                    input.removeAt(0)
                }.toList()
                Node13(*elements.toTypedArray())
            } else if (input[1].isDigit()) {
                Leaf13(input.removeAt(0).digitToInt() * 10 + input.removeAt(0).digitToInt())
            } else {
                Leaf13(input.removeAt(0).digitToInt())
            }
        }
        return readElement()
    }

    val signals = readInput("Day13").filterNot { it.isEmpty() }.map { s -> stringToTree(s.toMutableList()) }

    println(part1(signals.chunked(2).map { chunk -> Pair(chunk[0], chunk[1]) }))
    println(part2(signals))
}
