package gov.nasa.ltl.graph

import kotlin.system.exitProcess

object SynchronousProduct {

    fun dfs(g: Graph, nodes: Array<Array<Node?>>, nsets: Int, n0: Node, n1: Node) {
        val n = get(g, nodes, n0, n1)
        val t0 = n0.outgoingEdges
        val t1 = n1.outgoingEdges
        val i0 = t0.iterator()
        while (i0.hasNext()) {
            val e0 = i0.next()
            val next0 = e0.next
            var theEdge: Edge? = null
            var found = false
            val i1 = t1.iterator()
            while (i1.hasNext() && !found) {
                val e1 = i1.next()
                if (e1.getBooleanAttribute("else")) {
                    if (theEdge == null) {
                        theEdge = e1
                    }
                } else {
                    found = true
                    for (i in 0 until nsets) {
                        val b0 = e0.getBooleanAttribute("acc$i")
                        val b1 = e1.getBooleanAttribute("acc$i")
                        if (b1 && !b0) {
                            found = false
                            break
                        }
                    }
                }
                if (found) {
                    theEdge = e1
                }
            }
            if (theEdge != null) {
                val next1 = theEdge.next
                val newNext = isNew(nodes, next0, next1)
                val next = get(g, nodes, next0, next1)
                Edge(n!!, next!!, e0.guard, theEdge.action)
                if (newNext) {
                    dfs(g, nodes, nsets, next0, next1)
                }
            }
        }
    }

    fun product(g0: Graph, g1: Graph): Graph {
        val nsets = g0.getIntAttribute("nsets")
        if (nsets != g1.getIntAttribute("nsets")) {
            System.err.println("Different number of accepting sets")
            exitProcess(1)
        }
        val g = Graph()
        g.setStringAttribute("type", "ba")
        g.setStringAttribute("ac", "nodes")
        val nodes = Array(g0.nodeCount) {
            arrayOfNulls<Node>(g1.nodeCount)
        }
        dfs(g, nodes, nsets, g0.init!!, g1.init!!)
        return g
    }

    private fun isNew(nodes: Array<Array<Node?>>, n0: Node, n1: Node): Boolean {
        return nodes[n0.id][n1.id] == null
    }

    private fun get(g: Graph, nodes: Array<Array<Node?>>, n0: Node, n1: Node): Node? {
        return if (nodes[n0.id][n1.id] == null) {
            val n = Node(g)
            var label0 = n0.getStringAttribute("label")
            var label1 = n1.getStringAttribute("label")
            if (label0 == null) {
                label0 = n0.id.toString()
            }
            if (label1 == null) {
                label1 = n1.id.toString()
            }
            n.setStringAttribute("label", "$label0+$label1")
            if (n1.getBooleanAttribute("accepting")) {
                n.setBooleanAttribute("accepting", true)
            }
            n.also { nodes[n0.id][n1.id] = it }
        } else {
            nodes[n0.id][n1.id]
        }
    }

}