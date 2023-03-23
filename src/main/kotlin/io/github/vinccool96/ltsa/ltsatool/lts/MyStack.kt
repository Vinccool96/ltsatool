package io.github.vinccool96.ltsa.ltsatool.lts

class MyStack {

    protected var head: StackEntries? = null

    var depth = 0
        protected set

    fun empty(): Boolean {
        return head == null
    }

    fun push(var1: ByteArray?) {
        if (head == null) {
            head = StackEntries(null)
        } else if (head!!.full()) {
            head = StackEntries(head)
        }
        head!!.push(var1!!)
        ++depth
    }

    fun pop(): ByteArray? {
        val var1 = head!!.pop()
        --depth
        if (head!!.empty()) {
            head = head!!.next
        }
        return var1
    }

    fun peek(): ByteArray? {
        return head!!.peek()
    }

    fun mark() {
        head!!.mark()
    }

    fun marked(): Boolean {
        return head!!.marked()
    }

}