package io.github.vinccool96.ltsa.ltsatool.lts.ltl

import gov.nasa.ltl.graph.Graph
import io.github.vinccool96.ltsa.ltsatool.lts.Diagnostics
import io.github.vinccool96.ltsa.ltsatool.lts.LTSOutput
import io.github.vinccool96.ltsa.ltsatool.utils.toArrayOfNotNull
import java.util.*

class GeneralizedBuchiAutomata(var name: String?, var fac: FormulaFactory, var3: Vector<String>) {

    var nodes: MutableList<Node> = ArrayList()

    var formula: Formula? = fac.formula

    var untils: List<Until>? = null

    var maxId = -1

    var equivClasses: Array<Node?> = arrayOf()

    var states: Array<State?>? = null

    var naccept = 0

    var labelFactory: LabelFactory? = null

    init {
        labelFactory = LabelFactory(name, fac, var3)
    }

    fun translate() {
        Node.aut = this
        Node.factory = fac
        Transition.labelFactory = labelFactory
        naccept = fac.processUntils(formula!!, ArrayList<Until>().also {
            untils = it
        })
        val var1 = Node(formula!!)
        nodes = var1.expand(nodes)
        states = this.makeStates()
    }

    fun printNodes(var1: LTSOutput) {
        for (var2 in states!!.indices) {
            if (states!![var2] != null && var2 == states!![var2]!!.stateId) {
                states!![var2]!!.print(var1, naccept)
            }
        }
    }

    fun indexEquivalence(var1: Node): Int {
        var var2 = 0
        while (var2 < maxId && equivClasses[var2] != null) {
            if (equivClasses[var2]!!.next == var1.next) {
                return equivClasses[var2]!!.id
            }
            ++var2
        }
        if (var2 == maxId) {
            Diagnostics.fatal("size of equivalence classes array was incorrect")
        }
        equivClasses[var2] = var1
        return equivClasses[var2]!!.id
    }

    fun makeStates(): Array<State?> {
        val var1 = arrayOfNulls<State>(maxId)
        equivClasses = arrayOfNulls(maxId)
        val var2 = nodes.iterator()
        while (var2.hasNext()) {
            val var3 = var2.next()
            var3.equivId = indexEquivalence(var3)
            var3.makeTransitions(var1)
        }
        return var1
    }

    fun newId(): Int {
        return ++maxId
    }

    fun Gmake(): Graph {
        val var1 = Graph()
        var1.setStringAttribute("type", "gba")
        var1.setStringAttribute("ac", "edges")
        return if (states == null) {
            var1
        } else {
            val var2 = maxId
            val var3 = arrayOfNulls<gov.nasa.ltl.graph.Node>(var2)
            var var4 = 0
            while (var4 < var2) {
                if (states!![var4] != null && var4 == states!![var4]!!.stateId) {
                    var3[var4] = gov.nasa.ltl.graph.Node(var1)
                    var3[var4]!!.setStringAttribute("label", "S" + states!![var4]!!.stateId)
                }
                ++var4
            }
            var4 = 0
            while (var4 < var2) {
                if (states!![var4] != null && var4 == states!![var4]!!.stateId) {
                    states!![var4]!!.Gmake(var3.toArrayOfNotNull(), var3[var4]!!, naccept)
                }
                ++var4
            }
            if (naccept == 0) {
                var1.setIntAttribute("nsets", 1)
            } else {
                var1.setIntAttribute("nsets", naccept)
            }
            var1
        }
    }

}