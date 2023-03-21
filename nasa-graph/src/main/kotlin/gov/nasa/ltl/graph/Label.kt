package gov.nasa.ltl.graph

object Label {

    fun label(g: Graph): Graph {
        val type = g.getStringAttribute("type")!!
        val ac = g.getStringAttribute("ac")!!

        if (type == "gba") {
            if (ac == "nodes") {
                val nsets = g.getIntAttribute("nsets")
                g.forAllNodes(object : EmptyVisitor() {

                    override fun visitNode(node: Node) {
                        node.forAllEdges(object : EmptyVisitor() {

                            override fun visitEdge(edge: Edge) {
                                val n1 = edge.source

                                for (i in 0 until nsets) {
                                    if (n1.getBooleanAttribute("acc$i")) {
                                        edge.setBooleanAttribute("acc$i", true)
                                    }
                                }
                            }

                        })

                        for (i in 0 until nsets) {
                            node.setBooleanAttribute("acc$i", false)
                        }
                    }

                })
            }

            g.setStringAttribute("ac", "edges")
            return g
        } else {
            throw RuntimeException("invalid graph type: $type")
        }
    }

}