package io.github.vinccool96.ltsa.ltsatool.lts

class MyHashProg(var1: Int) : StackCheck {

    private var table: Array<MyHashProgEntry?> = arrayOfNulls(var1)

    private var count = 0

    constructor() : this(100001)

    fun add(var1: ByteArray, var2: MyHashProgEntry?) {
        val var3 = MyHashProgEntry(var1, var2)
        val var4: Int = StateCodec.hash(var1) % table.size
        var3.next = table[var4]
        table[var4] = var3
        ++count
    }

    operator fun get(var1: ByteArray): MyHashProgEntry? {
        val var2: Int = StateCodec.hash(var1) % table.size
        var var3 = table[var2]
        while (var3 != null) {
            if (StateCodec.equals(var3.key, var1)) {
                return var3
            }
            var3 = var3.next
        }
        return null
    }

    override fun onStack(var1: ByteArray): Boolean {
        val var2 = this[var1]
        return if (var2 == null) {
            false
        } else {
            var2.isReturn && !var2.isProcessed
        }
    }

    fun size(): Int {
        return count
    }

}