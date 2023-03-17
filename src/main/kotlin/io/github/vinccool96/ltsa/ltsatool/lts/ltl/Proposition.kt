package io.github.vinccool96.ltsa.ltsatool.lts.ltl

import io.github.vinccool96.ltsa.ltsatool.lts.Symbol

class Proposition(val sym: Symbol) : Formula() {

    override fun accept(visitor: Visitor): Formula? {
        return visitor.visit(this)
    }

    override val isLiteral: Boolean
        get() {
            return true
        }

    override fun toString(): String {
        return sym.toString()
    }

}