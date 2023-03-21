package io.github.vinccool96.ltsa.ltsatool.lts.ltl

import gov.nasa.ltl.graph.Edge
import gov.nasa.ltl.graph.Graph
import gov.nasa.ltl.graph.Node
import java.io.PrintStream

class ConverterGraph(val g: Graph) {

    fun printFSP(var1: PrintStream) {
        var var2 = false
        if (g.init != null) {
            var1.print("RES = S" + g.init!!.id)
        } else {
            var1.print("Empty")
            var2 = true
        }
        val var4 = g.nodes.iterator()
        while (var4.hasNext()) {
            var1.println(",")
            val var3 = var4.next()
            printNode(var3, var1)
        }
        var1.println(".")
        val var5 = g.getIntAttribute("nsets")
        if (var5 == 0 && !var2) {
            var var10 = true
            var1.print("AS = { ")
            val var11: Iterator<*> = g.nodes.iterator()
            while (var11.hasNext()) {
                val var12 = var11.next() as Node
                if (var12.getBooleanAttribute("accepting")) {
                    if (!var10) {
                        var1.print(", ")
                    } else {
                        var10 = false
                    }
                    var1.print("S" + var12.id)
                }
            }
            var1.println(" }")
        } else if (!var2) {
            for (var6 in 0 until var5) {
                var var7 = true
                var1.print("AS$var6 = { ")
                val var8: Iterator<*> = g.nodes.iterator()
                while (var8.hasNext()) {
                    val var9 = var8.next() as Node
                    if (var9.getBooleanAttribute("acc$var6")) {
                        if (!var7) {
                            var1.print(", ")
                        } else {
                            var7 = false
                        }
                        var1.print("S" + var9.id)
                    }
                }
                var1.println(" }")
            }
        }
        if (var1 !== System.out) {
            var1.close()
        }
    }

    fun printNode(var1: Node, var2: PrintStream) {
        var2.print("S" + var1.id + "=(")
        val var3 = var1.outgoingEdges.iterator()
        while (var3.hasNext()) {
            printEdge(var3.next(), var2)
            if (var3.hasNext()) {
                var2.print(" |")
            }
        }
        var2.print(")")
    }

    fun printEdge(var1: Edge, var2: PrintStream) {
        var var3 = ""
        val var4 = if (var1.guard == "-") {
            "TRUE"
        } else {
            var1.guard
        }
        val var5 = g.getIntAttribute("nsets")
        if (var5 == 0) {
            if (var1.getBooleanAttribute("accepting")) {
                var3 = "@"
            }
        } else {
            var var6 = true
            val var7 = StringBuffer()
            for (var8 in 0 until var5) {
                if (var1.getBooleanAttribute("acc$var8")) {
                    if (var6) {
                        var6 = false
                    } else {
                        var7.append(",")
                    }
                    var7.append(var8)
                }
            }
            if (!var6) {
                var3 = "{$var7}"
            }
        }
        var2.print(var4 + var3 + "-> S" + var1.next.id)
    }

}