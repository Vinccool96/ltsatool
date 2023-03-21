package gov.nasa.ltl.graph

object Simplify {

    fun simplify(g: Graph): Graph {
        var simplified: Boolean
        do {
            simplified = false
            val i = g.nodes.iterator()
            label133@ while (i.hasNext()) {
                val n0 = i.next()
                val j = g.nodes.iterator()
                while (true) {
                    var n1: Node
                    var equivalent: Boolean
                    var e1: Edge
                    do {
                        do {
                            do {
                                if (!j.hasNext()) {
                                    continue@label133
                                }
                                n1 = j.next()
                            } while (n1.id <= n0.id)
                        } while (n1.getBooleanAttribute("accepting") != n0.getBooleanAttribute("accepting"))
                        equivalent = true
                        var k = n0.outgoingEdges.iterator()
                        label90@ while (equivalent && k.hasNext()) {
                            val e0 = k.next()
                            equivalent = false
                            k = n1.outgoingEdges.iterator()
                            while (true) {
                                do {
                                    if (equivalent || !k.hasNext()) {
                                        continue@label90
                                    }
                                    e1 = k.next()
                                } while (e0.next != e1.next && (e0.next != n0 && e0.next != n1 || e1.next != n0 && e1.next != n1))
                                if (e0.guard == e1.guard && e0.action == e1.action) {
                                    equivalent = true
                                }
                            }
                        }
                        val k2 = n1.outgoingEdges.iterator()
                        label116@ while (equivalent && k2.hasNext()) {
                            val e1 = k2.next()
                            equivalent = false
                            val l: Iterator<*> = n0.outgoingEdges.iterator()
                            while (true) {
                                var e0: Edge
                                do {
                                    if (equivalent || !l.hasNext()) {
                                        continue@label116
                                    }
                                    e0 = l.next() as Edge
                                } while (e0.next != e1.next && (e0.next != n0 && e0.next != n1 || e1.next != n0 && e1.next != n1))
                                if (e0.guard == e1.guard && e0.action == e1.action) {
                                    equivalent = true
                                }
                            }
                        }
                    } while (!equivalent)
                    val k = n1.incomingEdges.iterator()
                    while (equivalent && k.hasNext()) {
                        e1 = k.next()
                        Edge(e1.source, n0, e1.guard, e1.action, e1.attributes)
                    }
                    n1.remove()
                    simplified = true
                }
            }
        } while (simplified)
        return g
    }

}