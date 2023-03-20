package gov.nasa.ltl.graph

open class EmptyVisitor(protected val arg: Any?) : Visitor {

    constructor() : this(null)

    override fun visitEdge(edge: Edge) {}

    override fun visitNode(node: Node) {}

}