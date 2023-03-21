package gov.nasa.ltl.graph

object Generate {

    fun generate(nsets: Int): Graph {
        val nnodes = nsets + 1
        val nodes = arrayOfNulls<Node>(nnodes)
        val g = Graph()
        g.setIntAttribute("nsets", nsets)
        g.setStringAttribute("type", "ba")
        g.setStringAttribute("ac", "nodes")
        var i = 0
        while (i < nnodes) {
            nodes[i] = Node(g)
            val label = StringBuffer()
            var j = 0
            while (j < i) {
                label.append("acc$i+")
                ++j
            }
            nodes[i]!!.setStringAttribute("label", label.toString())
            ++i
        }
        i = 0
        while (i < nsets) {
            var e: Edge
            for (j in nsets downTo i + 1) {
                e = Edge(nodes[i]!!, nodes[j]!!)
                var k = i
                while (k < j) {
                    e.setBooleanAttribute("acc$i", true)
                    ++k
                }
            }
            e = Edge(nodes[i]!!, nodes[i]!!)
            e.setBooleanAttribute("else", true)
            ++i
        }
        val n = nodes[nnodes - 1]
        n!!.setBooleanAttribute("accepting", true)
        var e = Edge(n, n)
        for (k in 0 until nsets) {
            e.setBooleanAttribute("acc$k", true)
        }
        i = nsets - 1
        while (i >= 0) {
            e = Edge(n, nodes[i]!!)
            if (i == 0) {
                e.setBooleanAttribute("else", true)
            } else {
                for (k in 0 until i) {
                    e.setBooleanAttribute("acc$k", true)
                }
            }
            --i
        }
        g.init = n
        return g
    }

}