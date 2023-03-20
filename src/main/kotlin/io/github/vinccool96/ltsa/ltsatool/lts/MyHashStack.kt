package io.github.vinccool96.ltsa.ltsatool.lts

class MyHashStack(var1: Int) : StackCheck {

    private val table: Array<MyHashStackEntry?> = arrayOfNulls(var1)

    private var count = 0

    private var depth = 0

    private var head: MyHashStackEntry? = null

    fun pushPut(var1: ByteArray) {
        val var2 = MyHashStackEntry(var1)
        val var3: Int = StateCodec.hash(var1) % table.size
        var2.next = table[var3]
        table[var3] = var2
        ++count
        var2.link = head
        head = var2
        ++depth
    }

    fun pop() {
        if (head != null) {
            head!!.marked = false
            head = head!!.link
            --depth
        }
    }

    fun peek(): ByteArray {
        return head!!.key
    }

    fun mark(var1: Int) {
        head!!.marked = true
        head!!.stateNumber = var1
    }

    fun marked(): Boolean {
        return head!!.marked
    }

    fun empty(): Boolean {
        return head == null
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

    override fun onStack(var1: ByteArray): Boolean {
        val var2: Int = StateCodec.hash(var1) % table.size
        var var3 = table[var2]
        while (var3 != null) {
            if (StateCodec.equals(var3.key, var1)) {
                return var3.marked
            }
            var3 = var3.next
        }
        return false
    }

    operator fun get(var1: ByteArray?): Int {
        val var2: Int = StateCodec.hash(var1!!) % table.size
        var var3 = table[var2]
        while (var3 != null) {
            if (StateCodec.equals(var3.key, var1)) {
                return var3.stateNumber
            }
            var3 = var3.next
        }
        return -99999
    }

    val size: Int
        get() {
            return count
        }

    val getDepth: Int
        get() {
            return depth
        }

}