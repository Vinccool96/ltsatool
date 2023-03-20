package gov.nasa.ltl.graph

interface Visitor {

    fun visitEdge(edge: Edge)

    fun visitNode(node: Node)

}