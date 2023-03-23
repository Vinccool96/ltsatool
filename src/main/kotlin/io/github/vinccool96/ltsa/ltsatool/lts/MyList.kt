package io.github.vinccool96.ltsa.ltsatool.lts

class MyList {

    protected var head: MyListEntry? = null

    protected var tail: MyListEntry? = null

    protected var count = 0

    fun add(var1: Int, var2: ByteArray?, var3: Int) {
        val var4 = MyListEntry(var1, var2, var3)
        if (head == null) {
            tail = var4
            head = tail
        } else {
            tail!!.next = var4
            tail = var4
        }
        ++count
    }

    operator fun next() {
        if (head != null) {
            head = head!!.next
        }
    }

    fun empty(): Boolean {
        return head == null
    }

    val from: Int
        get() {
            return head?.fromState ?: -1
        }

    val to: ByteArray?
        get() {
            return head?.toState
        }

    val action: Int
        get() {
            return head?.actionNo ?: -1
        }

    val size: Int
        get() {
            return count
        }

}