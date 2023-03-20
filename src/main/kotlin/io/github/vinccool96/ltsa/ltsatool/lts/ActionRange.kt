package io.github.vinccool96.ltsa.ltsatool.lts

import io.github.vinccool96.ltsa.ltsatool.lts.Diagnostics.fatal
import io.github.vinccool96.ltsa.ltsatool.lts.Expression.evaluate
import java.util.*

open class ActionRange(val rlow: Stack<Symbol>, val rhigh: Stack<Symbol>) : ActionLabels() {

    protected var current = 0

    protected var high = 0

    protected var low = 0

    constructor(range: Range) : this(range.low!!, range.high!!)

    override fun computeName(): String {
        return current.toString()
    }

    override fun initialise() {
        low = evaluate(rlow, locals, globals)
        high = evaluate(rhigh, locals, globals)
        if (low > high) {
            fatal("Range not defined", rlow.peek() as Symbol?)
        }
        current = low
    }

    override fun next() {
        ++current
    }

    override fun hasMoreNames(): Boolean {
        return current <= high
    }

    override fun make(): ActionLabels {
        return ActionRange(rlow, rhigh)
    }

}