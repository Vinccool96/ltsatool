package io.github.vinccool96.ltsa.ltsatool.lts

import io.github.vinccool96.ltsa.ltsatool.lts.Expression.getValue
import java.util.*

class ActionExpr(protected var expr: Stack<Symbol>) : ActionLabels() {

    protected var consumed = true

    override fun computeName(): String {
        val var1 = getValue(expr, locals!!, globals!!)
        return var1.toString()
    }

    override fun initialise() {
        consumed = false
    }

    override fun next() {
        consumed = true
    }

    override fun hasMoreNames(): Boolean {
        return !consumed
    }

    override fun make(): ActionLabels {
        return ActionExpr(expr)
    }

}