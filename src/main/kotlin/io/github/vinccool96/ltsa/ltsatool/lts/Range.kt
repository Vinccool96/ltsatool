package io.github.vinccool96.ltsa.ltsatool.lts

import java.util.*


class Range : Declaration() {

    var ranges: Hashtable<*, *>? = null

    var low: Stack<Symbol>? = null

    var high: Stack<Symbol>? = null

}