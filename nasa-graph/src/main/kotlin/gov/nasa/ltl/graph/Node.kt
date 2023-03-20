package gov.nasa.ltl.graph

import java.io.PrintStream
import java.util.*

class Node(val graph: Graph, attributes: Attributes) {

    var attributes: Attributes = attributes
        set(value) {
            val id = this.id
            field = Attributes(this.attributes)
            this.id = id
        }

    private val realOutgoingEdges: MutableList<Edge> = LinkedList()

    private val realIncomingEdges: MutableList<Edge> = LinkedList()

    constructor(graph: Graph) : this(graph, Attributes())

    constructor(node: Node) : this(node.graph, Attributes(node.attributes)) {
        for (edge in node.realOutgoingEdges) {
            Edge(this, edge)
        }
    }

    init {
        graph.addNode(this)
    }

    @Synchronized
    fun setBooleanAttribute(name: String, value: Boolean) {
        if (name == "_id") {
            return
        }
        attributes.setBoolean(name, value)
    }

    fun getBooleanAttribute(name: String?): Boolean {
        return attributes.getBoolean(name!!)
    }

    @get:Synchronized
    @set:Synchronized
    var id: Int
        get() {
            return this.attributes.getInt("_id")
        }
        internal set(value) {
            attributes.setInt("_id", id)
        }

    val incomingEdgeCount: Int
        get() {
            return this.realIncomingEdges.size
        }


    val incomingEdges: List<Edge>
        get() {
            return LinkedList(this.realIncomingEdges)
        }

    @Synchronized
    fun setIntAttribute(name: String, value: Int) {
        if (name == "_id") {
            return
        }
        attributes.setInt(name, value)
    }

    fun getIntAttribute(name: String?): Int {
        return attributes.getInt(name!!)
    }

    val outgoingEdgeCount: Int
        get() {
            return this.realOutgoingEdges.size
        }


    val outgoingEdges: List<Edge>
        get() {
            return LinkedList(this.realOutgoingEdges)
        }

    @Synchronized
    fun setStringAttribute(name: String, value: String?) {
        if (name == "_id") {
            return
        }
        attributes.setString(name, value!!)
    }

    fun getStringAttribute(name: String?): String? {
        return attributes.getString(name!!)
    }

    @Synchronized
    fun forAllEdges(v: Visitor) {
        val i: Iterator<Edge> = LinkedList(outgoingEdges).iterator()
        while (i.hasNext()) {
            v.visitEdge(i.next())
        }
    }

    @Synchronized
    fun remove() {
        run {
            val i = LinkedList(outgoingEdges).iterator()
            while (i.hasNext()) {
                i.next().remove()
            }
        }
        val i = LinkedList(incomingEdges).iterator()
        while (i.hasNext()) {
            i.next().remove()
        }
        graph.removeNode(this)
    }

    @Synchronized
    internal fun addIncomingEdge(edge: Edge) {
        realIncomingEdges.add(edge)
    }

    @Synchronized
    internal fun addOutgoingEdge(edge: Edge) {
        realOutgoingEdges.add(edge)
    }

    @Synchronized
    internal fun removeIncomingEdge(edge: Edge) {
        realIncomingEdges.remove(edge)
    }

    @Synchronized
    internal fun removeOutgoingEdge(edge: Edge) {
        realOutgoingEdges.remove(edge)
    }

    internal fun save(out: PrintStream, format: Int) {
        when (format) {
            Graph.SM_FORMAT -> saveSm(out)
            Graph.FSP_FORMAT -> saveFsp(out)
            Graph.XML_FORMAT -> saveXml(out)
            Graph.SPIN_FORMAT -> saveSpin(out)
            else -> throw RuntimeException("Unknown format!")
        }
    }

    private fun saveFsp(out: PrintStream) {
        out.print("S$id=(")
        val i = outgoingEdges.iterator()
        while (i.hasNext()) {
            i.next().save(out, Graph.FSP_FORMAT)
            if (i.hasNext()) {
                out.print(" |")
            }
        }

        out.print(")")
    }

    private fun saveSm(out: PrintStream) {
        val id: Int = this.id
        out.print("  ")
        out.println(outgoingEdges.size)
        attributes.unset("_id")
        out.print("  ")
        out.println(attributes)
        this.id = id
        val i = outgoingEdges.iterator()
        while (i.hasNext()) {
            i.next().save(out, Graph.SM_FORMAT)
        }
    }

    private fun saveSpin(out: PrintStream) {
        val ln = System.getProperty("line.separator")
        val lntab = "$ln     :: "
        if (getBooleanAttribute("accepting")) {
            out.print("accept_")
        }
        out.print("S$id:$ln     if$lntab")
        val i = outgoingEdges.iterator()
        while (i.hasNext()) {
            val e = i.next()
            e.save(out, Graph.SPIN_FORMAT)
            if (i.hasNext()) {
                out.print(lntab)
            }
        }
        out.print("$ln     fi;\n")
    }

    private fun saveXml(out: PrintStream) {
        val id: Int = this.id
        out.println("<node id=\"$id\">")
        attributes.unset("_id")
        attributes.save(out, Graph.XML_FORMAT)
        this.id = id
        val i = outgoingEdges.iterator()
        while (i.hasNext()) {
            i.next().save(out, Graph.XML_FORMAT)
        }
        out.println("</node>")
    }

    override fun toString(): String {
        return "S$id"
    }

}