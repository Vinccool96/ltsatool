package gov.nasa.ltl.graph

import java.util.*


object SCCReduction {

    fun reduce(g: Graph): Graph {
        val i = SCC.scc(g).iterator()
        while (i.hasNext()) {
            clearExternalEdges(i.next(), g)
        }
        var changed: Boolean
        label41@ do {
            changed = false
            val sccs = SCC.scc(g)
            val i = sccs.iterator()
            while (true) {
                while (true) {
                    if (!i.hasNext()) {
                        continue@label41
                    }
                    val scc = i.next()
                    val accepting = isAccepting(scc, g)
                    if (!accepting && isTerminal(scc)) {
                        changed = true
                        val j = scc.iterator()
                        while (j.hasNext()) {
                            j.next().remove()
                        }
                    } else if (isTransient(scc) || !accepting) {
                        changed = changed or anyAcceptingState(scc, g)
                        clearAccepting(scc, g)
                    }
                }
            }
        } while (changed)
        return g
    }

    private fun isAccepting(scc: List<Node>, g: Graph): Boolean {
        val type = g.getStringAttribute("type")
        val ac = g.getStringAttribute("ac")
        return if (type == "ba") {
            val i: Iterator<Node>
            if (ac == "nodes") {
                i = scc.iterator()
                while (i.hasNext()) {
                    if (i.next().getBooleanAttribute("accepting")) {
                        return true
                    }
                }
                false
            } else if (ac != "edges") {
                throw RuntimeException("invalid accepting type: $ac")
            } else {
                i = scc.iterator()
                while (i.hasNext()) {
                    val n = i.next()
                    val j = n.outgoingEdges.iterator()
                    while (j.hasNext()) {
                        val e = j.next()
                        if (e.getBooleanAttribute("accepting")) {
                            return true
                        }
                    }
                }
                false
            }
        } else if (type != "gba") {
            throw RuntimeException("invalid graph type: $type")
        } else {
            val nsets = g.getIntAttribute("nsets")
            val found = BitSet(nsets)
            var nsccs = 0
            val i: Iterator<Node>
            var n: Node
            if (ac == "nodes") {
                i = scc.iterator()
                while (i.hasNext()) {
                    n = i.next()
                    for (j in 0 until nsets) {
                        if (n.getBooleanAttribute("acc$j") && !found.get(j)) {
                            found.set(j)
                            ++nsccs
                        }
                    }
                }
            } else {
                if (ac != "edges") {
                    throw RuntimeException("invalid accepting type: $ac")
                }
                i = scc.iterator()
                while (i.hasNext()) {
                    n = i.next()
                    val j = n.outgoingEdges.iterator()
                    while (j.hasNext()) {
                        val e = j.next()
                        for (k in 0 until nsets) {
                            if (e.getBooleanAttribute("acc$k") && !found.get(k)) {
                                found.set(k)
                                ++nsccs
                            }
                        }
                    }
                }
            }
            nsccs == nsets
        }
    }

    private fun isTerminal(scc: List<Node>): Boolean {
        val i = scc.iterator()
        while (i.hasNext()) {
            val n = i.next()
            val j = n.outgoingEdges.iterator()
            while (j.hasNext()) {
                if (!scc.contains(j.next().next)) {
                    return false
                }
            }
        }
        return true
    }

    private fun isTransient(scc: List<Node>): Boolean {
        return if (scc.size != 1) {
            false
        } else {
            val n = scc[0]
            val i = n.outgoingEdges.iterator()
            while (i.hasNext()) {
                if (i.next().next == n) {
                    return false
                }
            }
            true
        }
    }

    private fun anyAcceptingState(scc: List<Node>, g: Graph): Boolean {
        val type = g.getStringAttribute("type")
        val ac = g.getStringAttribute("ac")
        if (type == "ba") {
            val i: Iterator<Node>
            var n: Node
            if (ac == "nodes") {
                i = scc.iterator()
                while (i.hasNext()) {
                    n = i.next()
                    if (n.getBooleanAttribute("accepting")) {
                        return true
                    }
                }
            } else {
                if (ac != "edges") {
                    throw RuntimeException("invalid accepting type: $ac")
                }
                i = scc.iterator()
                while (i.hasNext()) {
                    n = i.next()
                    val j = n.outgoingEdges.iterator()
                    while (j.hasNext()) {
                        val e = j.next()
                        if (e.getBooleanAttribute("accepting")) {
                            return true
                        }
                    }
                }
            }
        } else {
            if (type != "gba") {
                throw RuntimeException("invalid graph type: $type")
            }
            val nsets = g.getIntAttribute("nsets")
            val i: Iterator<Node>
            var n: Node
            if (ac == "nodes") {
                i = scc.iterator()
                while (i.hasNext()) {
                    n = i.next()
                    for (j in 0 until nsets) {
                        if (n.getBooleanAttribute("acc$j")) {
                            return true
                        }
                    }
                }
            } else {
                if (ac != "edges") {
                    throw RuntimeException("invalid accepting type: $ac")
                }
                i = scc.iterator()
                while (i.hasNext()) {
                    n = i.next()
                    val j = n.outgoingEdges.iterator()
                    while (j.hasNext()) {
                        val e = j.next()
                        for (k in 0 until nsets) {
                            if (e.getBooleanAttribute("acc$j")) {
                                return true
                            }
                        }
                    }
                }
            }
        }
        return false
    }

    private fun clearAccepting(scc: List<Node>, g: Graph) {
        val type = g.getStringAttribute("type")
        val ac = g.getStringAttribute("ac")
        if (type == "ba") {
            val i: Iterator<Node>
            var n: Node
            if (ac == "nodes") {
                i = scc.iterator()
                while (i.hasNext()) {
                    n = i.next()
                    n.setBooleanAttribute("accepting", false)
                }
            } else {
                if (ac != "edges") {
                    throw RuntimeException("invalid accepting type: $ac")
                }
                i = scc.iterator()
                while (i.hasNext()) {
                    n = i.next()
                    val j = n.outgoingEdges.iterator()
                    while (j.hasNext()) {
                        val e = j.next()
                        e.setBooleanAttribute("accepting", false)
                    }
                }
            }
        } else {
            if (type != "gba") {
                throw RuntimeException("invalid graph type: $type")
            }
            val nsets = g.getIntAttribute("nsets")
            val i: Iterator<Node>
            var n: Node
            if (ac == "nodes") {
                i = scc.iterator()
                while (i.hasNext()) {
                    n = i.next()
                    for (j in 0 until nsets) {
                        n.setBooleanAttribute("acc$j", false)
                    }
                }
            } else {
                if (ac != "edges") {
                    throw RuntimeException("invalid accepting type: $ac")
                }
                i = scc.iterator()
                while (i.hasNext()) {
                    n = i.next()
                    val j = n.outgoingEdges.iterator()
                    while (j.hasNext()) {
                        val e = j.next()
                        for (k in 0 until nsets) {
                            e.setBooleanAttribute("acc$k", false)
                        }
                    }
                }
            }
        }
    }

    private fun clearExternalEdges(scc: List<Node>, g: Graph) {
        val type = g.getStringAttribute("type")
        val ac = g.getStringAttribute("ac")
        if (type == "ba") {
            if (ac != "nodes") {
                if (ac != "edges") {
                    throw RuntimeException("invalid accepting type: $ac")
                }
                val i = scc.iterator()
                while (i.hasNext()) {
                    val n = i.next()
                    val j = n.outgoingEdges.iterator()
                    while (j.hasNext()) {
                        val e = j.next()
                        if (!scc.contains(e.next)) {
                            e.setBooleanAttribute("accepting", false)
                        }
                    }
                }
            }
        } else {
            if (type != "gba") {
                throw RuntimeException("invalid graph type: $type")
            }
            val nsets = g.getIntAttribute("nsets")
            if (ac != "nodes") {
                if (ac != "edges") {
                    throw RuntimeException("invalid accepting type: $ac")
                }
                val i = scc.iterator()
                label56@ while (i.hasNext()) {
                    val n = i.next()
                    val j = n.outgoingEdges.iterator()
                    while (true) {
                        var e: Edge
                        do {
                            if (!j.hasNext()) {
                                continue@label56
                            }
                            e = j.next()
                        } while (scc.contains(e.next))
                        for (k in 0 until nsets) {
                            e.setBooleanAttribute("acc$k", false)
                        }
                    }
                }
            }
        }
    }

}