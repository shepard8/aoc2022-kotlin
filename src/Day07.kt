import java.util.*
import kotlin.math.min

class Directory(val path: String) {
    val children = mutableListOf<Directory>()
    var totalSize: Int = 0
}

fun main() {
    fun part1(input: Directory): Int {
        val rec = input.children.sumOf { part1(it) }
        return (input.totalSize.takeIf { it < 100000 } ?: 0) + rec
    }

    fun findBest(input: Directory, minimumToDelete: Int, maximumToDelete: Int): Int {
        val dirSize = input.totalSize
        if (dirSize < minimumToDelete) return maximumToDelete
        val tempBest = min(maximumToDelete, dirSize)
        return input.children.minOf { findBest(it, minimumToDelete, tempBest) }
    }

    fun part2(input: Directory): Int {
        val needed = 30000000
        val unused = 70000000 - input.totalSize
        val minimumToDelete = needed - unused
        return findBest(input, minimumToDelete, unused)
    }

    val input = readInput("Day07").drop(1)
    val root = Directory("/")
    val dirStack = Stack<Directory>()
    dirStack.push(root)
    input.forEach { line ->
        if (line == "$ cd ..") {
            val popped = dirStack.pop()
            dirStack.peek().totalSize += popped.totalSize
        }
        else if (line.startsWith("$ cd ")) {
            val path = dirStack.peek().path + "/" + line.split(" ").last()
            val directory = Directory(path)
            dirStack.peek().children.add(directory)
            dirStack.push(directory)
        }
        else if (line == "$ ls") {
            // Do nothing
        }
        else if (line.startsWith("dir ")) {
            // Do nothing
        }
        else {
            dirStack.peek().totalSize += line.split(" ").first().toInt()
        }
    }

    println(part1(root))
    println(part2(root))
}
