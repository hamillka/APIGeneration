package api.random

import kotlin.random.Random

class RandomGenerator() {
    var _seed: Long = Random.nextLong()
    var seed
        get() = _seed
        set(v) { _seed = v }

    private val a = 25214903917
    private val c = 11
    private val m = (1L shl 48) - 1

    constructor(seed: Int) : this() {
        _seed = seed.toLong()
    }

    constructor(seed: Long) : this() {
        _seed = seed
    }

    constructor(seed: String) : this() {
        _seed = seed.hashCode().toLong()
    }

    fun nextInt(): Int {
        seed = (a * seed + c).and(m)
        return seed.toInt()
    }

    fun nextInt(low: Int, up: Int): Int {
//        [low, up)
        return low + nextInt().mod(up - low)
    }

    fun nextChar(): Char {
        return nextInt().mod(256).toChar()
    }

    fun nextAlpha(): Char {
        var res = nextChar()
        while (res !in 'a'..'z' && res !in 'A'..'Z') {
            res = nextChar()
        }
        return res
    }

    fun nextDouble(low: Double, up: Double): Double {
        val k = (nextInt().toDouble() / Int.MAX_VALUE + 1) / 2
        return low + k * (up - low)
    }

    fun nextBoolean(): Boolean {
        return nextInt() % 2 == 1
    }

    fun nextString(minLen: Int, maxLen: Int): String {
        val len = nextInt(minLen, maxLen)
        var res = ""
        for (i in 1..len) {
            res += nextAlpha()
        }
        return res
    }
}