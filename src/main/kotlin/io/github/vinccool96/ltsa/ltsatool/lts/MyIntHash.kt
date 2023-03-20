package io.github.vinccool96.ltsa.ltsatool.lts

class MyIntHash(size: Int) {

    private var table: Array<MyIntHashEntry?> = arrayOfNulls(size)

    private var count = 0

    fun put(var1: Int) {
        val var2 = MyIntHashEntry(var1)
        val var3: Int = var1 % table.size
        var2.next = table[var3]
        table[var3] = var2
        ++count
    }

    operator fun set(var1: Int, var2: Int) {
        val var3: Int = var1 % table.size
        var var4: MyIntHashEntry?
        var4 = table[var3]
        while (var4 != null) {
            if (var4.key == var1) {
                var4.value = var2
                return
            }
            var4 = var4.next
        }
        var4 = MyIntHashEntry(var1, var2)
        var4.next = table[var3]
        table[var3] = var4
        ++count
    }

    fun containsKey(var1: Int): Boolean {
        val var2: Int = var1 % table.size
        var var3 = table[var2]
        while (var3 != null) {
            if (var3.key == var1) {
                return true
            }
            var3 = var3.next
        }
        return false
    }

    operator fun get(var1: Int): Int {
        val var2: Int = var1 % table.size
        var var3 = table[var2]
        while (var3 != null) {
            if (var3.key == var1) {
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