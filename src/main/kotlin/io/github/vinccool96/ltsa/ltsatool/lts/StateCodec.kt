package io.github.vinccool96.ltsa.ltsatool.lts

class StateCodec(var1: IntArray) {

    var bitSize: IntArray

    var NBIT = 0

    var NBYTE = 0

    var boundaries: IntArray

    init {
        bitSize = IntArray(var1.size)
        NBIT = 0
        var var2 = 1
        var var3: Int
        var3 = var1.size - 1
        while (var3 >= 0) {
            bitSize[var3] = this.nbits(var1[var3] - 1)
            if (NBIT + bitSize[var3] > var2 * 64) {
                NBIT = var2 * 64
                ++var2
            }
            NBIT += bitSize[var3]
            --var3
        }
        NBYTE = NBIT / 8
        if (NBIT % 8 > 0) {
            ++NBYTE
        }
        boundaries = IntArray(var2)
        var3 = 0
        var var4 = 0
        for (var5 in var1.indices.reversed()) {
            if (var3 + bitSize[var5] <= 64) {
                var3 += bitSize[var5]
            } else {
                boundaries[var4] = var5 + 1
                var3 = bitSize[var5]
                ++var4
            }
        }
        boundaries[var4] = 0
    }

    private fun longToBytes(var1: ByteArray, var2: Long, var4: Int, var5: Int) {
        var res = var2
        for (var6 in var4 until var5) {
            var1[var6] = (var1[var6].toInt() or res.toInt().toByte().toInt()).toByte()
            res = res ushr 8
        }
    }

    private fun bytesToLong(var1: ByteArray, var2: Int, var3: Int): Long {
        var var4 = 0L
        for (var6 in var3 - 1 downTo var2) {
            var4 = var4 or (var1[var6].toLong() and 255L)
            if (var6 > var2) {
                var4 = var4 shl 8
            }
        }
        return var4
    }

    fun bits(): Int {
        var var1 = 0
        for (element in bitSize) {
            var1 += element
        }
        return var1
    }

    fun zero(): ByteArray {
        return ByteArray(NBYTE)
    }

    fun encode(var1: IntArray): ByteArray? {
        val var2 = ByteArray(NBYTE)
        var var3: Int = bitSize.size - 1
        var var4 = NBYTE
        for (var5 in boundaries.indices) {
            var var6 = 0L
            var var8: Int
            var8 = var3
            while (var8 >= boundaries[var5]) {
                if (var1[var8] < 0) {
                    return null
                }
                var6 = var6 or var1[var8].toLong()
                if (var8 > boundaries[var5]) {
                    var6 = var6 shl bitSize[var8 - 1]
                }
                --var8
            }
            var8 = var4 - 8
            if (var8 < 0) {
                var8 = 0
            }
            longToBytes(var2, var6, var8, var4)
            var4 = var8
            var3 = boundaries[var5] - 1
        }
        return var2
    }

    fun decode(var1: ByteArray): IntArray {
        val var2 = IntArray(bitSize.size + 1)
        var var3: Int = bitSize.size
        var var4 = NBYTE
        for (var5 in boundaries.indices) {
            var var6 = var4 - 8
            if (var6 < 0) {
                var6 = 0
            }
            var var7 = bytesToLong(var1, var6, var4)
            for (var9 in boundaries[var5] until var3) {
                var2[var9] = var7.toInt() and masks[bitSize[var9]]
                var7 = var7 ushr bitSize[var9]
            }
            var4 = var6
            var3 = boundaries[var5]
        }
        return var2
    }

    private fun nbits(var1: Int): Int {
        var var0 = var1
        var var2 = 0
        while (var0 != 0) {
            var0 = var0 ushr 1
            ++var2
        }
        return var2
    }

    companion object {

        var masks =
                intArrayOf(0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535, 131071,
                        262143, 524287, 1048575, 2097151, 4194303, 8388607, 16777215, 33554431, 67108863, 134217727,
                        268435455, 536870911, 1073741823, Int.MAX_VALUE)

        fun hash(var0: ByteArray): Int {
            var var1 = 0L
            var var3 = 0
            while (var3 < var0.size) {
                var1 = var1 * 127L + var0[var3].toLong()
                ++var3
            }
            var3 = (var1 xor (var1 ushr 32)).toInt()
            return var3 and Int.MAX_VALUE
        }

        fun hashLong(var0: ByteArray): Long {
            var var1 = 0L
            for (var3 in var0.indices) {
                var1 = var1 * 255L + var0[var3].toLong()
            }
            return var1
        }

        fun equals(var0: ByteArray?, var1: ByteArray?): Boolean {
            return if (var0 == null && var1 == null) {
                true
            } else if (var0 != null && var1 != null) {
                if (var0.size != var1.size) {
                    true
                } else {
                    for (var2 in var0.indices) {
                        if (var0[var2] != var1[var2]) {
                            return false
                        }
                    }
                    true
                }
            } else {
                false
            }
        }

    }

}