package io.github.vinccool96.ltsa.ltsatool.lts

import java.util.*

class PartialOrder {

    private val machines: Array<CompactState>
    private val actionSharedBy: Array<IntArray>
    private val checker: StackChecker? = null
    private val candidates: Array<IntArray>
    private val partners: Array<IntArray>
    private val Nactions = 0
    private val names: Array<String>
    private val visible: BitSet? = null
    private val preserveOE = false
    private val high: BitSet? = null

}