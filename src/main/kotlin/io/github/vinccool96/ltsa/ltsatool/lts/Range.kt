package io.github.vinccool96.ltsa.ltsatool.lts

import java.util.*


class Range : Declaration() {

    var low: Stack<Symbol>? = null

    var high: Stack<Symbol>? = null

    companion object {

        var ranges: Hashtable<String, Range>? = null

    }

}