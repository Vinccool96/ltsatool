package io.github.vinccool96.ltsa.ltsatool.lts.ltl

class Next(val next: Formula) : Formula() {

    override fun accept(visitor: Visitor): Formula? {
        return visitor.visit(this)
    }

    override fun toString(): String {
        return "X$next"
    }

}