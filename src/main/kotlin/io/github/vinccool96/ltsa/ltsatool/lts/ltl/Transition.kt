package io.github.vinccool96.ltsa.ltsatool.lts.ltl

import gov.nasa.ltl.graph.Edge
import gov.nasa.ltl.graph.Node
import io.github.vinccool96.ltsa.ltsatool.lts.LTSOutput
import java.util.*

class Transition(var propositions: SortedSet<Proposition>, var pointsTo: Int, var3: BitSet, var safe_acc: Boolean) {

    var accepting: BitSet = BitSet()

    init {
        accepting.or(var3)
    }

    fun goesTo(): Int {
        return pointsTo
    }

    fun computeAccepting(var1: Int): BitSet {
        val var2 = BitSet(var1)
        for (var3 in 0 until var1) {
            if (!accepting[var3]) {
                var2.set(var3)
            }
        }
        return var2
    }

    fun print(var1: LTSOutput, var2: Int) {
        if (propositions.isEmpty()) {
            var1.out("LABEL True")
        } else { // TODO
            // Node.printFormulaSet(var1, "LABEL", propositions)
        }
        var1.out(" T0 " + goesTo())
        if (var2 > 0) {
            var1.outln(" Acc " + computeAccepting(var2))
        } else if (safe_acc) {
            var1.outln(" Acc {0}")
        } else {
            var1.outln("")
        }
    }

    fun Gmake(var1: Array<Node>, var2: Node, var3: Int) {
        var var4 = "-"
        val var5 = "-"
        if (!propositions.isEmpty()) {
            var4 = lf!!.makeLabel(propositions)
        }
        val var6 = Edge(var2, var1[pointsTo], var4, var5)
        if (var3 == 0) {
            var6.setBooleanAttribute("acc0", true)
        } else {
            for (var7 in 0 until var3) {
                if (!accepting[var7]) {
                    var6.setBooleanAttribute("acc$var7", true)
                }
            }
        }
    }

    companion object {

        var lf: LabelFactory? = null

    }

}