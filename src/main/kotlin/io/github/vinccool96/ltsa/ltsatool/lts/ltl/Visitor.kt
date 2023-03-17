package io.github.vinccool96.ltsa.ltsatool.lts.ltl

interface Visitor {

    fun visit(var1: True): Formula?

    fun visit(var1: False): Formula?

    fun visit(var1: Proposition): Formula?

    fun visit(var1: Not): Formula?

    fun visit(var1: And): Formula?

    fun visit(var1: Or): Formula?

    fun visit(var1: Until): Formula?

    fun visit(var1: Release): Formula?

    fun visit(var1: Next): Formula?

}