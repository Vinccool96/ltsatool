package gov.nasa.ltl.graph

import java.util.*
import kotlin.math.min


object SCC {

    fun print(sccs: List<MutableList<Node>>) {
        println("Strongly connected components:")
        var cnt = 0
        val i = sccs.iterator()
        while (i.hasNext()) {
            val scc = i.next()
            println("\tSCC #" + cnt++)
            val j = scc.iterator()
            while (j.hasNext()) {
                val n = j.next()
                println("\t\t" + n.id + " - " + n.getStringAttribute("label"))
            }
        }
    }

    fun scc(g: Graph): List<MutableList<Node>> {
        val init = g.init
        return if (init == null) {
            LinkedList()
        } else {
            init.setBooleanAttribute("_reached", true)
            val s = SCCState()
            visit(init, s)
            val scc = Array<MutableList<Node>>(s.scc) { _ -> LinkedList<Node>() }
            g.forAllNodes(object : EmptyVisitor() {
                override fun visitNode(node: Node) {
                    scc[node.getIntAttribute("_scc")].add(node)
                    node.setBooleanAttribute("_reached", false)
                    node.setBooleanAttribute("_dfsnum", false)
                    node.setBooleanAttribute("_low", false)
                    node.setBooleanAttribute("_scc", false)
                }
            })
            val list = LinkedList<MutableList<Node>>()
            for (i in 0 until s.scc) {
                list.add(scc[i])
            }
            list
        }
    }

    private fun visit(p: Node, s: SCCState) {
        s.l.add(0, p)
        p.setIntAttribute("_dfsnum", s.n)
        p.setIntAttribute("_low", s.n)
        ++s.n
        val i = p.outgoingEdges.iterator()
        while (i.hasNext()) {
            val e = i.next()
            val q = e.next
            if (!q.getBooleanAttribute("_reached")) {
                q.setBooleanAttribute("_reached", true)
                visit(q, s)
                p.setIntAttribute("_low", min(p.getIntAttribute("_low"), q.getIntAttribute("_low")))
            } else if (q.getIntAttribute("_dfsnum") < p.getIntAttribute("_dfsnum") && s.l.contains(q)) {
                p.setIntAttribute("_low", min(p.getIntAttribute("_low"), q.getIntAttribute("_dfsnum")))
            }
        }
        if (p.getIntAttribute("_low") == p.getIntAttribute("_dfsnum")) {
            var v: Node
            do {
                v = s.l.removeAt(0)
                v.setIntAttribute("_scc", s.scc)
            } while (v != p)
            ++s.scc
        }
    }

    private class SCCState {

        var n = 0

        var scc = 0

        var l: MutableList<Node> = LinkedList()

    }

}