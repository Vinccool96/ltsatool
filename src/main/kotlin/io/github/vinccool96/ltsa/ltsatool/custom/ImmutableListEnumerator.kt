package io.github.vinccool96.ltsa.ltsatool.custom

import java.util.*

class ImmutableListEnumerator<T>(private var current: ImmutableList<T>?) : Enumeration<T> {

    override fun hasMoreElements(): Boolean {
        return current != null
    }

    override fun nextElement(): T {
        return if (current != null) {
            val var1 = current!!.item
            current = current!!.next
            var1
        } else {
            throw NoSuchElementException("ImmutableListEnumerator")
        }
    }

}