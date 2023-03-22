package io.github.vinccool96.ltsa.ltsatool.lts

import java.util.*

class MyHashQueue(var1: Int) : StackCheck {

    private var table = arrayOfNulls<MyHashQueueEntry>(var1)

    private var count = 0

    private var head: MyHashQueueEntry? = null

    private var tail: MyHashQueueEntry? = null

    fun addPut(var1: ByteArray, var2: Int, var3: MyHashQueueEntry?) {
        val var4 = MyHashQueueEntry(var1, var2, var3)
        if (var3 != null) {
            var4.level = var3.level + 1
        }
        val var5: Int = StateCodec.hash(var1) % table.size
        var4.next = table[var5]
        table[var5] = var4
        ++count
        if (head == null) {
            tail = var4
            head = tail
        } else {
            tail!!.link = var4
            tail = var4
        }
    }

    fun peek(): MyHashQueueEntry? {
        return head
    }

    fun pop() {
        head = head!!.link
        if (head == null) {
            tail = head
        }
    }

    fun empty(): Boolean {
        return head == null
    }

    fun containsKey(var1: ByteArray?): Boolean {
        val var2: Int = StateCodec.hash(var1!!) % table.size
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
                return var3.level <= head!!.level
            }
            var3 = var3.next
        }
        return false
    }

    val size: Int
        get() {
            return count
        }

    fun getPath(var1: MyHashQueueEntry?, var2: Array<String>): LinkedList<String> {
        var var4 = var1
        val var3 = LinkedList<String>()
        while (var4 != null) {
            if (var4.parent != null) {
                var3.addFirst(var2[var4.action])
            }
            var4 = var4.parent
        }
        return var3
    }

    fun depth(var1: MyHashQueueEntry): Int {
        var var3: MyHashQueueEntry? = var1
        var var2 = 0
        while (var3 != null) {
            ++var2
            var3 = var3.parent
        }
        return var2
    }

}