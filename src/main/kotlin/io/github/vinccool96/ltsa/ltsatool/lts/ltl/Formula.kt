package io.github.vinccool96.ltsa.ltsatool.lts.ltl

import java.util.*

abstract class Formula : Comparable<Formula> {
    var id = -1
    private var untilIndex = -1
    private var rightOfWhichUntil: BitSet? = null
    var visited = false

    open fun getUI(): Int {
        return untilIndex
    }

    open fun setUI(var1: Int) {
        untilIndex = var1
    }

    open fun setRofUI(var1: Int) {
        if (rightOfWhichUntil == null) {
            rightOfWhichUntil = BitSet()
        }
        rightOfWhichUntil!!.set(var1)
    }

    open fun getRofWU(): BitSet? {
        return rightOfWhichUntil
    }

    val isRightOfUntil: Boolean
        get() {
            return rightOfWhichUntil != null
        }

    override operator fun compareTo(other: Formula): Int {
        return id - other.id
    }

    abstract fun accept(visitor: Visitor): Formula?

    open val isLiteral: Boolean
        get() {
            return false
        }

    open fun getSub1(): Formula? {
        return accept(Sub1.get())
    }

    open fun getSub2(): Formula? {
        return accept(Sub2.get())
    }
}