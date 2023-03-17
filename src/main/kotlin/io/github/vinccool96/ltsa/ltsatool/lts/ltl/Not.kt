package io.github.vinccool96.ltsa.ltsatool.lts.ltl

class Not(val next: Formula) : Formula() {

    override fun accept(visitor: Visitor): Formula? {
        return visitor.visit(this)
    }

    override val isLiteral: Boolean
        get() {
            return next.isLiteral
        }

    override fun toString(): String {
        return "!$next"
    }

}