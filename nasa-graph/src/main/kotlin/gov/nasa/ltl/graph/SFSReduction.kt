package gov.nasa.ltl.graph

import java.util.*

object SFSReduction {

    fun reduce(g: Graph): Graph {
        var prevNumColors = 1
        var currNumPO = 3
        var prevNumPO = 1
        var newColorSet: TreeSet<ColorPair>? = null
        var newColorList: LinkedList<BasePair<ColorPair>>? = null
        var accepting = false
        var nonaccepting = false
        val nodes = g.nodes
        for (currNode in nodes) {
            currNode.setIntAttribute("_prevColor", 1)
            if (isAccepting(currNode)) {
                currNode.setIntAttribute("_currColor", 1)
                accepting = true
            } else {
                currNode.setIntAttribute("_currColor", 2)
                nonaccepting = true
            }
        }
        var currNumColors: Int
        currNumColors = if (accepting && nonaccepting) {
            2
        } else {
            1
        }
        var currPO = Array(2) { BooleanArray(2) }
        for (i in 0..1) {
            for (j in 0..1) {
                currPO[i][j] = i >= j
            }
        }
        var currPairOne: ColorPair
        while (currNumColors != prevNumColors || currNumPO != prevNumPO) {
            for (currNode in nodes) {
                currNode.setIntAttribute("_prevColor", currNode.getIntAttribute("_currColor"))
            }
            val prevPO = currPO
            prevNumColors = currNumColors
            newColorList = LinkedList()
            newColorSet = TreeSet()
            for (currNode in nodes) {
                val currPair = ColorPair(currNode.getIntAttribute("_prevColor"), getPrevN(currNode, prevPO))
                newColorList.add(BasePair(currNode.id, currPair))
                newColorSet.add(currPair)
            }
            currNumColors = newColorSet.size
            val ordered = LinkedList<ColorPair>()
            for (currPair in newColorSet) {
                ordered.add(currPair)
            }
            for (cPair in newColorList) {
                currPairOne = cPair.element
                g.getNode(cPair.value)!!.setIntAttribute("_currColor", ordered.indexOf(currPairOne) + 1)
            }
            prevNumPO = currNumPO
            currNumPO = 0
            currPO = Array(currNumColors) { BooleanArray(currNumColors) }
            label131@ for (cPairOne in newColorList) {
                currPairOne = cPairOne.element
                val j = newColorList.iterator()
                while (true) {
                    while (true) {
                        if (!j.hasNext()) {
                            continue@label131
                        }
                        val currPairTwo = j.next().element
                        val po = prevPO[currPairTwo.color - 1][currPairOne.color - 1]
                        val dominate = iDominateSet(currPairOne.iMaxSet, currPairTwo.iMaxSet, prevPO)
                        if (po && dominate) {
                            currPO[ordered.indexOf(currPairTwo)][ordered.indexOf(currPairOne)] = true
                            ++currNumPO
                        } else {
                            currPO[ordered.indexOf(currPairTwo)][ordered.indexOf(currPairOne)] = false
                        }
                    }
                }
            }
        }
        val result: Graph
        if (newColorList == null) {
            result = g
            return reachabilityGraph(result)
        } else {
            result = Graph()
            val newNodes = arrayOfNulls<Node>(currNumColors)
            for (i in 0 until currNumColors) {
                val n = Node(result)
                newNodes[i] = n
            }
            val i = newColorList.iterator()
            while (true) {
                var origNodeId: Int
                do {
                    if (!i.hasNext()) {
                        return reachabilityGraph(result)
                    }
                    val nodePair = i.next()
                    origNodeId = nodePair.value
                    currPairOne = nodePair.element
                } while (!newColorSet!!.contains(currPairOne))
                newColorSet.remove(currPairOne)
                val pairSet = currPairOne.iMaxSet
                val color = currPairOne.color
                val currNode = newNodes[color - 1]
                val j = pairSet.iterator()
                while (j.hasNext()) {
                    val neigh = j.next()
                    val neighPos = neigh.color - 1
                    Edge(currNode!!, newNodes[neighPos]!!, neigh.transition)
                }
                if (g.init!!.id == origNodeId) {
                    result.init = currNode
                }
                if (isAccepting(g.getNode(origNodeId))) {
                    currNode!!.setBooleanAttribute("accepting", true)
                }
            }
        }
    }

    private fun isAccepting(nodeIn: Node?): Boolean {
        return nodeIn!!.getBooleanAttribute("accepting")
    }

    private fun getPrevN(currNode: Node, prevPO: Array<BooleanArray>): TreeSet<ITypeNeighbor> {
        val edges = currNode.outgoingEdges
        val neighbors = LinkedList<ITypeNeighbor>()
        val prevN = TreeSet<ITypeNeighbor>()
        val i = edges.iterator()
        var iNeigh: ITypeNeighbor
        while (i.hasNext()) {
            val currEdge = i.next()
            iNeigh = ITypeNeighbor(currEdge.next.getIntAttribute("_prevColor"), currEdge.guard)
            neighbors.add(iNeigh)
        }
        return if (neighbors.size == 0) {
            prevN
        } else {
            do {
                var useless = false
                iNeigh = neighbors.removeFirst()
                val nI = neighbors.iterator()
                while (nI.hasNext()) {
                    val nNeigh = nI.next()
                    val dominating = iDominates(iNeigh, nNeigh, prevPO)
                    if (dominating == iNeigh) {
                        nI.remove()
                    }
                    if (dominating == nNeigh) {
                        useless = true
                        break
                    }
                }
                if (!useless) {
                    prevN.add(iNeigh)
                }
            } while (neighbors.size > 0)
            prevN
        }
    }

    private fun iDominateSet(setOne: TreeSet<ITypeNeighbor>, setTwo: TreeSet<ITypeNeighbor>,
            prevPO: Array<BooleanArray>): Boolean {
        val working = TreeSet(setTwo)
        val i = working.iterator()
        while (true) {
            while (i.hasNext()) {
                val neighTwo = i.next()
                val j = setOne.iterator()
                while (j.hasNext()) {
                    val neighOne = j.next()
                    val dominating = iDominates(neighOne, neighTwo, prevPO)
                    if (dominating == neighOne) {
                        i.remove()
                        break
                    }
                }
            }
            return working.size == 0
        }
    }

    private fun iDominates(iNeigh: ITypeNeighbor, nNeigh: ITypeNeighbor, prevPO: Array<BooleanArray>): ITypeNeighbor? {
        val iTerm = iNeigh.transition
        val nTerm = nNeigh.transition
        val iColor = iNeigh.color
        val nColor = nNeigh.color
        val theSubterm = subterm(iTerm, nTerm)
        return if (theSubterm === iTerm) {
            if (prevPO[nColor - 1][iColor - 1]) iNeigh else null
        } else if (theSubterm === nTerm) {
            if (prevPO[iColor - 1][nColor - 1]) nNeigh else null
        } else {
            if (theSubterm == "true") {
                if (prevPO[nColor - 1][iColor - 1]) {
                    return iNeigh
                }
                if (prevPO[iColor - 1][nColor - 1]) {
                    return nNeigh
                }
            }
            null
        }
    }

    private fun reachabilityGraph(g: Graph): Graph {
        val work = Vector<Node>()
        val reachable = Vector<Node>()
        work.add(g.init)
        while (!work.isEmpty()) {
            val currNode = work.firstElement()
            reachable.add(currNode)
            if (currNode != null) {
                val outgoingEdges = currNode.outgoingEdges
                val i = outgoingEdges.iterator()
                while (i.hasNext()) {
                    val currEdge = i.next()
                    val nextNode = currEdge.next
                    if (!work.contains(nextNode) && !reachable.contains(nextNode)) {
                        work.add(nextNode)
                    }
                }
            }
            if (work.removeAt(0) !== currNode) {
                println("ERROR")
            }
        }
        val nodes = g.nodes
        val i = nodes.iterator()
        while (i.hasNext()) {
            val n = i.next()
            if (!reachable.contains(n)) {
                g.removeNode(n)
            }
        }
        return g
    }

    private fun subterm(pred1: String, pred2: String): String {
        return if (pred1 == "-" && pred2 == "-") {
            "true"
        } else if (pred1 == "-") {
            pred1
        } else if (pred2 == "-") {
            pred2
        } else if (pred1.indexOf("true") != -1 && pred2.indexOf("true") != -1) {
            "true"
        } else if (pred1.indexOf("true") != -1) {
            pred1
        } else if (pred2.indexOf("true") != -1) {
            pred2
        } else {
            val alphaStr: String
            val tauStr: String
            if (pred1.length <= pred2.length) {
                alphaStr = pred1
                tauStr = pred2
            } else {
                alphaStr = pred2
                tauStr = pred1
            }
            val alphaTk = StringTokenizer(alphaStr, "&")
            val tauTk = StringTokenizer(tauStr, "&")
            val tauLst = LinkedList<String>()
            var alphaLit: String
            while (tauTk.hasMoreTokens()) {
                alphaLit = tauTk.nextToken()
                tauLst.add(alphaLit)
            }
            while (alphaTk.hasMoreTokens()) {
                alphaLit = alphaTk.nextToken()
                if (!tauLst.contains(alphaLit)) {
                    return "false"
                }
            }
            if (pred1.length == pred2.length) "true" else alphaStr
        }
    }

}