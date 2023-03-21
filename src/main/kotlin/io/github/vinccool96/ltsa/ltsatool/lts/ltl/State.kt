package io.github.vinccool96.ltsa.ltsatool.lts.ltl

import gov.nasa.ltl.graph.Node
import io.github.vinccool96.ltsa.ltsatool.lts.LTSOutput
import java.util.*

class State(private var transitions: MutableList<Transition>, var stateId: Int) : Comparable<State> {

    constructor() : this(LinkedList(), -1)

    constructor(stateId: Int) : this(LinkedList(), stateId)

    override operator fun compareTo(other: State): Int {
        return if (this !== other) 1 else 0
    }

    fun add(var1: Transition) {
        transitions.add(var1)
    }

    fun print(var1: LTSOutput, var2: Int) {
        var1.outln("STATE $stateId")
        val var3 = transitions.iterator()
        while (var3.hasNext()) {
            var3.next().print(var1, var2)
        }
    }

    fun Gmake(var1: Array<Node>, var2: Node, var3: Int) {
        val var4 = transitions.listIterator(0)
        while (var4.hasNext()) {
            val var6 = var4.next()
            var6.Gmake(var1, var2, var3)
        }
    }

}