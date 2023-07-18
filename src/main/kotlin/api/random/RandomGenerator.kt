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

    fun nextIntIn(low: Int, up: Int): Int {
        return low + nextInt() % (up - low)
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

    fun nextDoubleIn(low: Double, up: Double): Double {
        // Should be checked
        return low + (nextInt().toDouble() / Int.MAX_VALUE) * (up - low)
    }

    fun nextBoolean(): Boolean {
        return nextInt() % 2 == 1
    }
}