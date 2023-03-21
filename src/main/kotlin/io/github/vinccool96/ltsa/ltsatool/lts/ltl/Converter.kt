package io.github.vinccool96.ltsa.ltsatool.lts.ltl

import gov.nasa.ltl.graph.Edge
import gov.nasa.ltl.graph.Graph
import gov.nasa.ltl.graph.Node
import io.github.vinccool96.ltsa.ltsatool.lts.CompactState
import io.github.vinccool96.ltsa.ltsatool.lts.Diagnostics
import io.github.vinccool96.ltsa.ltsatool.lts.EventState
import java.io.PrintStream
import java.util.*

class Converter(var1: String?, var g: Graph, var3: LabelFactory) : CompactState() {

    private var accepting = this.acceptance

    var iacc = 0

    init {
        this.name = var1
        alphabet = var3.makeAlphabet()
        this.makeStates(var3)
    }

    private fun makeStates(var1: LabelFactory) {
        maxStates = g.nodeCount + iacc + 1
        states = arrayOfNulls(maxStates)
        val var2 = var1.transLabels
        addTrueNode(maxStates - 1, var2)
        val var3 = g.nodes.iterator()
        while (var3.hasNext()) {
            addNode(var3.next(), var2)
        }
        if (iacc == 1) {
            states[0] = EventState.union(states[0], states[1])
        }
        addAccepting()
        reachable()
    }

    private fun addAccepting() {
        for (var1 in 0 until maxStates - 1) {
            if (accepting[var1]) {
                states[var1 + iacc] = EventState.add(states[var1 + iacc], EventState(alphabet.size - 1, var1 + iacc))
            }
        }
    }

    fun addNode(var1: Node, var2: HashMap<String, BitSet>) {
        val var3 = var1.id
        val var4 = BitSet(alphabet.size - 2)
        val var5 = var1.outgoingEdges.iterator()
        while (var5.hasNext()) {
            addEdge(var5.next(), var3, var2, var4)
        }
        complete(var3, var4)
    }

    fun addTrueNode(var1: Int, var2: HashMap<String, BitSet>) {
        val var3 = var2["true"]!!
        for (var4 in 0 until var3.size()) {
            if (var3[var4]) {
                states[var1] = EventState.add(states[var1], EventState(var4 + 1, var1))
            }
        }
    }

    fun complete(var1: Int, var2: BitSet) {
        for (var3 in 0 until alphabet.size - 2) {
            if (!var2[var3]) {
                states[var1 + iacc] = EventState.add(states[var1 + iacc], EventState(var3 + 1, maxStates - 1))
            }
        }
    }

    fun addEdge(var1: Edge, var2: Int, var3: HashMap<String, BitSet>, var4: BitSet) {
        val var5 = if (var1.guard == "-") {
            "true"
        } else {
            var1.guard
        }
        val var6 = var3[var5]!!
        var4.or(var6)
        for (var7 in 0 until var6.size()) {
            if (var6[var7]) {
                states[var2 + iacc] = EventState.add(states[var2 + iacc], EventState(var7 + 1, var1.next.id + iacc))
            }
        }
    }

    fun printFSP(var1: PrintStream) {
        if (g.init != null) {
            var1.print(name + " = S" + g.init!!.id)
        } else {
            var1.print("Empty")
        }
        val var4: Iterator<*> = g.nodes.iterator()
        while (var4.hasNext()) {
            var1.println(",")
            val var3 = var4.next() as Node
            printNode(var3, var1)
        }
        var1.println(".")
        if (var1 !== System.out) {
            var1.close()
        }
    }

    val acceptance: BitSet
        get() {
            val var1 = BitSet()
            val var2 = g.getIntAttribute("nsets")
            if (var2 > 0) {
                Diagnostics.fatal("More than one acceptance set")
            }
            val var3 = g.nodes.iterator()
            while (var3.hasNext()) {
                val var4 = var3.next()
                if (var4.getBooleanAttribute("accepting")) {
                    var1.set(var4.id)
                }
            }
            return var1
        }

    fun printNode(var1: Node, var2: PrintStream) {
        val var3 = if (accepting[var1.id]) "@" else ""
        var2.print("S" + var1.id + var3 + " =(")
        val var4 = var1.outgoingEdges.iterator()
        while (var4.hasNext()) {
            printEdge(var4.next(), var2)
            if (var4.hasNext()) {
                var2.print(" |")
            }
        }
        var2.print(")")
    }

    fun printEdge(var1: Edge, var2: PrintStream) {
        val var4 = if (var1.guard == "-") {
            "true"
        } else {
            var1.guard
        }
        var2.print(var4 + " -> S" + var1.next.id)
    }

}