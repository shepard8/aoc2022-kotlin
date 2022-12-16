import java.util.PriorityQueue

fun main() {
    class Valve(val name: String, val rate: Int, val accessible: MutableList<Valve>) {
        override fun toString() = name
    }

    class State(val currentValve: Valve, val openedValves: List<Valve>, val currentRate: Int, val currentOut: Int, val timeElapsed: Int) {
        fun open() =
            if (currentValve in openedValves) State(currentValve, openedValves, currentRate, currentOut + currentRate, timeElapsed + 1)
            else State(currentValve, openedValves + currentValve, currentRate + currentValve.rate, currentOut + currentRate, timeElapsed + 1)
        fun goTo(valve: Valve) = State(valve, openedValves, currentRate, currentOut + currentRate, timeElapsed + 1)
        override fun toString() = "$currentValve - $currentRate - $currentOut - $timeElapsed"
        fun potential() = currentOut + 20 * (30 - timeElapsed)
    }

    class State2(val myValve: Valve, val elephantValve: Valve, val openedValves: List<Valve>, val seenValves: Set<Valve>, val currentRate: Int, val currentOut: Int, val timeElapsed: Int) {
        fun move(elephantMove: Valve, myMove: Valve): State2 {
            val newlyOpened = listOfNotNull(
                elephantValve.takeIf { elephantMove == elephantValve && elephantValve !in openedValves },
                myValve.takeIf { myMove == myValve && myValve != elephantValve && myValve !in openedValves },
            )
            return State2(
                myMove, elephantMove,
                openedValves + newlyOpened,
                seenValves + (setOf(myValve, elephantValve) - newlyOpened.toSet()),
                currentRate + newlyOpened.sumOf { it.rate },
                currentOut + currentRate,
                timeElapsed + 1
            )
        }
        override fun toString() = "$myValve - $elephantValve - $currentRate - $currentOut - $timeElapsed"
        fun potential(): Int {
            return currentOut + (26 - timeElapsed) * currentRate + (if (myValve !in seenValves) 10000 else 0) + (if (elephantValve !in seenValves) 10000 else 0)
        }
    }

    fun parseData(input: List<String>, print: Boolean): List<Valve> {
        val valves = mutableMapOf<String, Valve>()

        if (print) println("graph {")
        input.forEach { line ->
            val regex = Regex("^Valve (..) has flow rate=(\\d+); tunnels? leads? to valves? (.+)$")
            val match = regex.matchEntire(line)!!
            val values = match.groupValues.drop(1)

            if (print) {
                val label = "${values[0]} (${values[1]})"
                val color = if (values[1] == "0") "black" else "red"
                println("${values[0]} [label=\"$label\",color=$color]")
                values[2].split(", ").filter { it > values[0] }.forEach {
                    println("${values[0]} -- $it")
                }
            }

            val valve = Valve(values[0], values[1].toInt(), values[2].split(", ").mapNotNull { valves[it] }.toMutableList())
            valves[values[0]] = valve
        }
        if (print) println("}")

        valves.values.forEach { valve -> valve.accessible.forEach { valves[it.name]!!.accessible.add(valve) } }

        return valves.values.toList()
    }

    fun part1(input: List<String>): Int {
        val valves = parseData(input, true)
        val start = State(
            valves.find { it.name == "AA" }!!,
            listOf(),
            0,
            0,
            0
        )
        val toExplore = PriorityQueue<State> { s1, s2 -> s2.potential() - s1.potential() }
        toExplore.add(start)

        var maxOut = 0
        var i: Long = 0

        while (toExplore.isNotEmpty()) {
            val state = toExplore.poll()
            if (++i % 100_000_000 == 0.toLong()) {
                println("$i : ${toExplore.count()} left to explore - Next state : $state - Current max - $maxOut")
            }
            if (state.currentOut > maxOut) {
                maxOut = state.currentOut
                println(state)
            }
            if (state.timeElapsed == 30)
                continue
            if (state.currentValve.rate > 0 && state.currentValve !in state.openedValves) {
                toExplore.add(state.open())
            }
            state.currentValve.accessible.forEach {
                toExplore.add(state.goTo(it))
            }
        }

        return maxOut
    }

    fun part2(input: List<String>): Int {
        val valves = parseData(input, true)
        val start = State2(
            valves.find { it.name == "AA" }!!,
            valves.find { it.name == "AA" }!!,
            listOf(),
            setOf(),
            0,
            0,
            0
        )
        val toExplore = PriorityQueue<State2> { s1, s2 -> s2.potential() - s1.potential() }
        toExplore.add(start)

        var maxOut = 0
        var i: Long = 0

        while (toExplore.isNotEmpty()) {
            val state = toExplore.poll()
            if (++i % 100_000_000 == 0.toLong()) {
                println("$i : ${toExplore.count()} left to explore - Next state : $state - Current max - $maxOut")
            }
            if (state.currentOut > maxOut) {
                maxOut = state.currentOut
                println(state)
            }
            // Prune
            if (state.currentOut + 217 * (26 - state.timeElapsed) <= maxOut)
                continue
            val elephantMoves = if (state.elephantValve.rate > 0 && state.elephantValve !in state.openedValves) listOf(state.elephantValve) else state.elephantValve.accessible
            val myMoves = if (state.myValve.rate > 0 && state.myValve !in state.openedValves) listOf(state.myValve) else state.myValve.accessible
            elephantMoves.forEach { eMove ->
                myMoves.filter { it != eMove }.forEach { mMove ->
                    toExplore.add(state.move(eMove, mMove))
                }
            }
        }

        return maxOut
    }

    val input = readInput("Day16")


    // Uncomment either of these lines. Answer comes after a few minutes for part 1 and a few seconds for part 2. I don't feel like managing this mess, sorry :-]
//    println(part1(input))
    println(part2(input))
}
