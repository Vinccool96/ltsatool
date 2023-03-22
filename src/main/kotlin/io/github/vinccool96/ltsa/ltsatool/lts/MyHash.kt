package io.github.vinccool96.ltsa.ltsatool.lts

class MyHash(var1: Int) : StackCheck {

    private var table: Array<MyHashEntry?> = arrayOfNulls(var1)

    private var count = 0

    fun put(var1: ByteArray) {
        val var2 = MyHashEntry(var1)
        val var3: Int = StateCodec.hash(var1) % table.size
        var2.next = table[var3]
        table[var3] = var2
        ++count
    }

    fun put(var1: ByteArray, var2: Int) {
        val var3: Int = StateCodec.hash(var1) % table.size
        var var4: MyHashEntry?
        var4 = table[var3]
        while (var4 != null) {
            if (StateCodec.equals(var4.key, var1)) {
                var4.value = var2
                return
            }
            var4 = var4.next
        }
        var4 = MyHashEntry(var1, var2)
        var4.next = table[var3]
        table[var3] = var4
        ++count
    }

    fun remove(var1: ByteArray) {
        val var2: Int = StateCodec.hash(var1) % table.size
        var var3 = table[var2]
        var var4 = var3
        while (var3 != null) {
            if (StateCodec.equals(var3.key, var1)) {
                if (var4 == table[var2]) {
                    table[var2] = var3.next
                } else {
                    var4 = var3.next
                }
                return
            }
            var4 = var3
            var3 = var3.next
        }
    }

    override fun onStack(var1: ByteArray): Boolean {
        return this.containsKey(var1)
    }

    fun containsKey(var1: ByteArray): Boolean {
        val var2: Int = StateCodec.hash(var1) % table.size
        var var3 = table[var2]
        while (var3 != null) {
            if (StateCodec.equals(var3.key, var1)) {
                return true
            }
            var3 = var3.next
        }
        return false
    }

    operator fun get(var1: ByteArray): Int {
        val var2: Int = StateCodec.hash(var1) % table.size
        var var3 = table[var2]
        while (var3 != null) {
            if (StateCodec.equals(var3.key, var1)) {
                return var3.value
            }
            var3 = var3.next
        }
        return -99999
    }

    fun size(): Int {
        return count
    }

}