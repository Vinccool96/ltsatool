package io.github.vinccool96.ltsa.ltsatool.lts.ltl

abstract class Eventually(val next: Formula) : Formula() {

    override fun toString(): String {
        return "<>$next"
    }

}