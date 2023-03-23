package io.github.vinccool96.ltsa.ltsatool.lts

class StackEntries(var next: StackEntries?) {

    val value = arrayOfNulls<ByteArray>(N)

    val marks = BooleanArray(N)

    var index = 0

    fun empty(): Boolean {
        return index == 0
    }

    fun full(): Boolean {
        return index == N
    }

    fun push(var1: ByteArray) {
        value[index] = var1
        marks[index] = false
        ++index
    }

    fun pop(): ByteArray? {
        --index
        return value[index]
    }

    fun peek(): ByteArray? {
        return value[index - 1]
    }

    fun mark() {
        marks[index - 1] = true
    }

    fun marked(): Boolean {
        return marks[index - 1]
    }

    companion object {

        const val N = 1024

    }

}