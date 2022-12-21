enum class Operator { Plus, Minus, Times, Divide }

fun main() {
    abstract class Monkey(val name: String)

    class Number(name: String, val number: Long): Monkey(name)

    // After solving part 2 I realize monkey1 and monkey2 should be of type Monkey instead of String.
    // That would have allowed more object-oriented solving of part 2, putting backtrack in class Operation instead of some standalone function.
    class Operation(name: String, val monkey1: String, val monkey2: String, val op: Operator): Monkey(name) {
        fun toNumber(val1: Long, val2: Long): Number {
            val number = Number(name, when (op) {
                Operator.Plus -> val1 + val2
                Operator.Minus -> val1 - val2
                Operator.Times -> val1 * val2
                Operator.Divide -> val1 / val2
            })
            return number
        }
    }

    fun parse(line: String): Monkey {
        val regex = Regex("^([a-z]{4}): ((\\d+)|([a-z]{4}) (.) ([a-z]{4}))$")
        val match = regex.matchEntire(line)!!
        val values = match.groupValues.drop(1)
        return if (values[2].isEmpty()) {
            val op = when (values[4][0]) {
                '+' -> Operator.Plus
                '-' -> Operator.Minus
                '*' -> Operator.Times
                '/' -> Operator.Divide
                else -> throw IllegalArgumentException()
            }
            Operation(values[0], values[3], values[5], op)
        }
        else {
            Number(values[0], values[2].toLong())
        }
    }

    fun monkeyForward(monkeys: List<Monkey>, monkey: Monkey): Monkey {
        return if (monkey is Operation) {
            val monkey1 = monkeys.find { it.name == monkey.monkey1 }
            val monkey2 = monkeys.find { it.name == monkey.monkey2 }
            if (monkey1 is Number && monkey2 is Number) {
                monkey.toNumber(monkey1.number, monkey2.number)
            }
            else monkey
        }
        else monkey
    }

    fun part1(input: List<String>): Long {
        val monkeys = input.map { parse(it) }.toMutableList()
        while (monkeys.find { it.name == "root" } is Operation) {
            monkeys.replaceAll { monkeyForward(monkeys, it) }
        }
        return (monkeys.find { it.name == "root" } as Number).number
    }

    fun monkeyBackward(monkeys: List<Monkey>, monkey: Operation, value: Long): Long {
        if (monkey.name == "humn") return value
        val monkey1 = monkeys.first { it.name == monkey.monkey1 }
        val monkey2 = monkeys.first { it.name == monkey.monkey2 }
        return if (monkey1 is Number && monkey2 is Operation) {
            when (monkey.op) {
                Operator.Plus -> monkeyBackward(monkeys, monkey2, value - monkey1.number)
                Operator.Minus -> monkeyBackward(monkeys, monkey2, monkey1.number - value)
                Operator.Times -> monkeyBackward(monkeys, monkey2, value / monkey1.number)
                Operator.Divide -> monkeyBackward(monkeys, monkey2, monkey1.number / value)
            }
        }
        else if (monkey1 is Operation && monkey2 is Number) {
            when (monkey.op) {
                Operator.Plus -> monkeyBackward(monkeys, monkey1, value - monkey2.number)
                Operator.Minus -> monkeyBackward(monkeys, monkey1, value + monkey2.number)
                Operator.Times -> monkeyBackward(monkeys, monkey1, value / monkey2.number)
                Operator.Divide -> monkeyBackward(monkeys, monkey1, value * monkey2.number)
            }
        }
        else {
            throw IllegalStateException()
        }
    }

    fun part2(input: List<String>): Long {
        val humn = Operation("humn", "humn", "zero", Operator.Plus)
        val zero = Number("zero", 0)
        val monkeys = (input.map { parse(it) }.filterNot { it.name == "humn" } + listOf(humn, zero)).toMutableList()
        val root = monkeys.find { it.name == "root" } as Operation
        while (monkeys.find { it.name == root.monkey1 } is Operation && monkeys.find { it.name == root.monkey2 } is Operation) {
            monkeys.replaceAll { monkeyForward(monkeys, it) }
        }

        return monkeyBackward(monkeys,
            monkeys.filter { it.name == root.monkey1 || it.name == root.monkey2 }.filterIsInstance<Operation>().first(),
            monkeys.filter { it.name == root.monkey1 || it.name == root.monkey2 }.filterIsInstance<Number>().first().number
        )
    }

    val input = readInput("Day21")
    println(part1(input))
    println(part2(input))
}
