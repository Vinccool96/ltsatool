package io.github.vinccool96.ltsa.ltsatool.lts.ltl

class Release(val left: Formula, val right: Formula) : Formula() {

    override fun accept(visitor: Visitor): Formula? {
        return visitor.visit(this)
    }

    override fun toString(): String {
        return "($left R $right)"
    }

}