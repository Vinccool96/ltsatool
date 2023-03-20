package io.github.vinccool96.ltsa.ltsatool.lts.ltl

abstract class Implies(val left: Formula, val right: Formula) : Formula() {

    override fun toString(): String {
        return "($left => $right)"
    }

}