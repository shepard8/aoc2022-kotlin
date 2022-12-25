fun main() {
    fun snafu2dec(input: String): Long {
        var ret = 0L
        input.forEach {
            ret = 5 * ret + when (it) {
                '2' -> 2L
                '1' -> 1L
                '0' -> 0L
                '-' -> -1L
                '=' -> -2L
                else -> throw IllegalArgumentException()
            }
        }
        return ret
    }

    fun dec2snafu(v: Long): String {
        if (v == 0L) return ""

        val lastDigit = when (v % 5) {
            0L -> '0'
            1L -> '1'
            2L -> '2'
            3L -> '='
            4L -> '-'
            else -> throw IllegalStateException()
        }
        val remind = when (v % 5) {
            0L, 1L, 2L -> 0L
            3L, 4L -> 1L
            else -> throw IllegalStateException()
        }

        return dec2snafu(v / 5L + remind) + lastDigit
    }

    fun part1(input: List<String>): String {
        val dec = input.sumOf { snafu2dec(it) }
        val snafu = dec2snafu(dec)
        println(dec)
        println(snafu2dec(snafu))
        return snafu
    }

    val input = readInput("Day25")
    println(part1(input))
}
