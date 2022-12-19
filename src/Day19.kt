import kotlin.math.ceil
import kotlin.math.sqrt

fun main() {
    data class Blueprint(
        val id: Int,
        val oreRobotCost: Int,
        val clayRobotCost: Int,
        val obsidianRobotCostInOre: Int,
        val obsidianRobotCostInClay: Int,
        val geodeRobotCostInOre: Int,
        val geodeRobotCostInObsidian: Int
    )

    class State(
        val blueprint: Blueprint,
        val minute: Int,
        val end: Int,
        val oreRobots: Int,
        val clayRobots: Int,
        val obsidianRobots: Int,
        val geodeRobots: Int,
        val ore: Int,
        val clay: Int,
        val obsidian: Int,
        val geode: Int
    ) {
        override fun toString(): String {
            return "State($minute : $ore ore [+ $oreRobots], $clay clay [+ $clayRobots], $obsidian obsidian [+ $obsidianRobots], $geode geode [+ $geodeRobots])"
        }

        fun waitUntilEnd(): State {
            val minutesToWait = end - minute
            return State(blueprint, minute + minutesToWait, end,
                oreRobots, clayRobots, obsidianRobots, geodeRobots,
                ore + minutesToWait * oreRobots,
                clay + minutesToWait * clayRobots,
                obsidian + minutesToWait * obsidianRobots,
                geode + minutesToWait * geodeRobots
            )
        }

        fun buildOreRobot(): State? {
            val minutesToWait = 1 + maxOf(0, ceili(blueprint.oreRobotCost - ore, oreRobots))
            if (minute + minutesToWait > end) return null
            return State(blueprint, minute + minutesToWait, end,
                oreRobots + 1, clayRobots, obsidianRobots, geodeRobots,
                ore + minutesToWait * oreRobots - blueprint.oreRobotCost,
                clay + minutesToWait * clayRobots,
                obsidian + minutesToWait * obsidianRobots,
                geode + minutesToWait * geodeRobots
            )
        }

        fun buildClayRobot(): State? {
            val minutesToWait = 1 + maxOf(0, ceili(blueprint.clayRobotCost - ore, oreRobots))
            if (minute + minutesToWait > end) return null
            return State(blueprint, minute + minutesToWait, end,
                oreRobots, clayRobots + 1, obsidianRobots, geodeRobots,
                ore + minutesToWait * oreRobots - blueprint.clayRobotCost,
                clay + minutesToWait * clayRobots,
                obsidian + minutesToWait * obsidianRobots,
                geode + minutesToWait * geodeRobots
            )
        }

        fun buildObsidianRobot(): State? {
            if (clayRobots == 0) return null
            val minutesToWait = 1 + maxOf(0, ceili(blueprint.obsidianRobotCostInOre - ore, oreRobots), ceili(blueprint.obsidianRobotCostInClay - clay, clayRobots))
            if (minute + minutesToWait > end) return null
            return State(blueprint, minute + minutesToWait, end,
                oreRobots, clayRobots, obsidianRobots + 1, geodeRobots,
                ore + minutesToWait * oreRobots - blueprint.obsidianRobotCostInOre,
                clay + minutesToWait * clayRobots - blueprint.obsidianRobotCostInClay,
                obsidian + minutesToWait * obsidianRobots,
                geode + minutesToWait * geodeRobots
            )
        }

        fun buildGeodeRobot(): State? {
            if (obsidianRobots == 0) return null
            val minutesToWait = 1 + maxOf(0, ceili(blueprint.geodeRobotCostInOre - ore, oreRobots), ceili(blueprint.geodeRobotCostInObsidian - obsidian, obsidianRobots))
            if (minute + minutesToWait > end) return null
            return State(blueprint, minute + minutesToWait, end,
                oreRobots, clayRobots, obsidianRobots, geodeRobots + 1,
                ore + minutesToWait * oreRobots - blueprint.geodeRobotCostInOre,
                clay + minutesToWait * clayRobots,
                obsidian + minutesToWait * obsidianRobots - blueprint.geodeRobotCostInObsidian,
                geode + minutesToWait * geodeRobots
            )
        }

        private fun ceili(numerator: Int, denominator: Int): Int {
            if (numerator <= 0) return 0
            return numerator / denominator + if (numerator % denominator == 0) 0 else 1
        }
    }

    abstract class Strategy {
        abstract fun accessibleStates(state: State): List<State>
    }

    class ExploreAllStrategy: Strategy() {
        override fun accessibleStates(state: State): List<State> {
            return listOfNotNull(
                state.buildGeodeRobot(),
                state.buildObsidianRobot(),
                state.buildClayRobot(),
                state.buildOreRobot(),
                state.waitUntilEnd()
            )
        }
    }

    class PrunedExploreAllStrategy(blueprint: Blueprint, end: Int): Strategy() {
        val maxOreRobots = maxOf(blueprint.clayRobotCost, blueprint.obsidianRobotCostInOre, blueprint.geodeRobotCostInOre)
        val geodeRobotBefore = end - 1
        val obsidianRobotBefore = geodeRobotBefore - triangularRoot(blueprint.geodeRobotCostInObsidian)
        val clayRobotBefore = obsidianRobotBefore - triangularRoot(blueprint.obsidianRobotCostInClay)

        private fun triangularRoot(x: Int): Int {
            return ceil(sqrt((8.0 * x + 1) - 1) / 2).toInt()
        }

        override fun accessibleStates(state: State): List<State> {
            val geodeRobotState = state.buildGeodeRobot()
            if (geodeRobotState != null && geodeRobotState.minute == state.minute + 1) {
                return listOf(geodeRobotState)
            }
            return listOfNotNull(
                state.buildGeodeRobot(),
                state.buildObsidianRobot(),
                state.buildClayRobot(),
                state.buildOreRobot(),
                state.waitUntilEnd())
                .filter { it.oreRobots <= maxOreRobots }
                .filter { it.minute < geodeRobotBefore || it.geodeRobots > 0 }
                .filter { it.minute < obsidianRobotBefore || it.obsidianRobots > 0 }
                .filter { it.minute < clayRobotBefore || it.clayRobots > 0 }
        }
    }

    class BuildStrongestStrategy: Strategy() {
        override fun accessibleStates(state: State): List<State> {
            return listOfNotNull(
                state.buildGeodeRobot(),
                state.buildObsidianRobot(),
                state.buildClayRobot(),
                state.buildOreRobot(),
                state
            ).take(1)
        }
    }

    class ExampleStrategy: Strategy() {
        val builds = mutableListOf<(State) -> State>(
            { it.buildClayRobot()!! },
            { it.buildClayRobot()!! },
            { it.buildClayRobot()!! },
            { it.buildObsidianRobot()!! },
            { it.buildClayRobot()!! },
            { it.buildObsidianRobot()!! },
            { it.buildGeodeRobot()!! },
            { it.buildGeodeRobot()!! },
            { it.waitUntilEnd() },
        )

        override fun accessibleStates(state: State): List<State> {
            return listOf(builds.removeFirst()(state))
        }
    }

    fun maxes(blueprints: List<Blueprint>, minutes: Int): List<Int> {
        return blueprints.map { blueprint ->
            val strategy = PrunedExploreAllStrategy(blueprint, minutes)
            val start = State(blueprint, 0, minutes, 1, 0, 0, 0, 0, 0, 0, 0)
            val finalStates = mutableListOf<State>()
            val stack = ArrayDeque<State>()
            var tests = 0
            stack.add(start)
            while (stack.isNotEmpty()) {
                ++tests
                val state = stack.removeFirst()
                if (state.minute == minutes) {
                    finalStates.add(state)
                    continue
                }
                stack.addAll(strategy.accessibleStates(state))
            }
            val best = finalStates.maxOfOrNull { it.geode } ?: 0
            println("Max for ${blueprint.id}: $best found in $tests tests.")
            best
        }
    }

    fun part1(blueprints: List<Blueprint>): Int {
        return maxes(blueprints, 24).zip(blueprints).sumOf { (best, blueprint) -> blueprint.id * best }
    }

    fun part2(blueprints: List<Blueprint>): Int {
        return maxes(blueprints, 32).reduce { a, b -> a * b }
    }

    fun readBlueprint(line: String): Blueprint {
        val regex = Regex("^Blueprint (\\d+): Each ore robot costs (\\d) ore. Each clay robot costs (\\d) ore. Each obsidian robot costs (\\d) ore and (\\d+) clay. Each geode robot costs (\\d) ore and (\\d+) obsidian.$")
        val match = regex.matchEntire(line)!!
        val values = match.groupValues.drop(1).map { it.toInt() }
        return Blueprint(values[0], values[1], values[2], values[3], values[4], values[5], values[6])
    }

    val blueprints = readInput("Day19").map { line ->
        readBlueprint(line)
    }
    println(part1(blueprints))
    println(part2(blueprints.take(3)))
}
