package gov.nasa.ltl.graph

object SuperSetReduction {

    fun reduce(g: Graph): Graph {
        val nsets = g.getIntAttribute("nsets")
        val type = g.getStringAttribute("type")
        val ac = g.getStringAttribute("ac")
        return if (type != "gba") {
            throw RuntimeException("invalid graph type: $type")
        } else {
            val nedges: Int
            val asets: Array<BooleanArray>
            var nNsets: Int
            if (ac == "nodes") {
                nedges = g.nodeCount
                asets = Array(nsets) { BooleanArray(nedges) }
                g.forAllNodes(object : EmptyVisitor() {
                    override fun visitNode(node: Node) {
                        for (i in 0 until nsets) {
                            val acc = "acc$i"
                            if (node.getBooleanAttribute(acc)) {
                                asets[i][node.id] = true
                                node.setBooleanAttribute(acc, false)
                            }
                        }
                    }
                })
                val remove = BooleanArray(nsets)
                for (i in 0 until nsets) {
                    nNsets = 0
                    while (nNsets < nsets && !remove[i]) {
                        if (i != nNsets && !remove[nNsets] && included(asets[nNsets], asets[i])) {
                            remove[i] = true
                        }
                        ++nNsets
                    }
                }
                nNsets = 0
                while (nNsets < nsets) {
                    if (!remove[nNsets]) {
                        ++nNsets
                    }
                    ++nNsets
                }
                val n_asets = Array(nNsets) {
                    BooleanArray(nedges)
                }
                nNsets = 0
                for (i in 0 until nsets) {
                    if (!remove[i]) {
                        n_asets[nNsets++] = asets[i]
                    }
                }
                g.setIntAttribute("nsets", nNsets)
                var i = 0
                while (i < nedges) {
                    val n = g.getNode(i)
                    for (j in 0 until nNsets) {
                        if (n_asets[j][i]) {
                            n!!.setBooleanAttribute("acc$j", true)
                        }
                    }
                    ++i
                }
                g
            } else if (ac != "edges") {
                throw RuntimeException("invalid accepting type: $ac")
            } else {
                nedges = g.edgeCount
                asets = Array(nsets) { BooleanArray(nedges) }
                val edges = arrayOfNulls<Edge>(nedges)
                g.forAllEdges(object : EmptyVisitor(0) {
                    override fun visitEdge(edge: Edge) {
                        val id = arg as Int
                        arg = id + 1
                        edges[id] = edge
                        for (i in 0 until nsets) {
                            val acc = "acc$i"
                            if (edge.getBooleanAttribute(acc)) {
                                asets[i][id] = true
                                edge.setBooleanAttribute(acc, false)
                            }
                        }
                    }
                })
                val remove = BooleanArray(nsets)
                nNsets = 0
                while (nNsets < nsets) {
                    var nNNsets = 0
                    while (nNNsets < nsets && !remove[nNNsets]) {
                        if (nNNsets != nNsets && !remove[nNNsets] && included(asets[nNNsets], asets[nNsets])) {
                            remove[nNNsets] = true
                        }
                        ++nNNsets
                    }
                    ++nNsets
                }
                nNsets = 0
                for (i in 0 until nsets) {
                    if (!remove[i]) {
                        ++nNsets
                    }
                }
                val n_asets = Array(nNsets) {
                    BooleanArray(nedges)
                }
                nNsets = 0
                var idx = 0
                while (idx < nsets) {
                    if (!remove[idx]) {
                        n_asets[nNsets++] = asets[idx]
                    }
                    ++idx
                }
                g.setIntAttribute("nsets", nNsets)
                for (i in 0 until nedges) {
                    val e = edges[i]
                    for (j in 0 until nNsets) {
                        if (n_asets[j][i]) {
                            e!!.setBooleanAttribute("acc$j", true)
                        }
                    }
                }
                g
            }
        }
    }

    private fun included(a: BooleanArray, b: BooleanArray): Boolean {
        val al = a.size
        val bl = b.size
        if (al > bl) {
            return false
        }

        for (i in 0 until al) {
            if (a[i] && !b[i]) {
                return false
            }
        }
        return true
    }

}