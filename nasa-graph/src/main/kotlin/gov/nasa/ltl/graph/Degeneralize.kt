package gov.nasa.ltl.graph

import gov.nasa.ltl.graph.Generate.generate

object Degeneralize {

    fun degeneralize(g: Graph): Graph {
        var graph = g
        val nsets = graph.getIntAttribute("nsets")
        val type = graph.getStringAttribute("type")
        if (type == "gba") {
            val ac = graph.getStringAttribute("ac")
            val d: Graph
            if (ac == "nodes") {
                if (nsets == 1) {
                    accept(graph)
                } else {
                    Label.label(graph)
                    d = generate(nsets)
                    graph = SynchronousProduct.product(graph, d)
                }
            } else if (ac == "edges") {
                d = generate(nsets)
                graph = SynchronousProduct.product(graph, d)
            }
        } else if (type != "ba") {
            throw RuntimeException("invalid graph type: $type")
        }
        return graph
    }

    private fun accept(g: Graph) {
        g.setBooleanAttribute("nsets", false)
        g.forAllNodes(object : EmptyVisitor() {

            override fun visitNode(node: Node) {
                if (node.getBooleanAttribute("acc0")) {
                    node.setBooleanAttribute("accepting", true)
                    node.setBooleanAttribute("acc0", false)
                }
            }

        })
    }

}