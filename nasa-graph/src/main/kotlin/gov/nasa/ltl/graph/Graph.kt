package gov.nasa.ltl.graph

import java.io.*
import java.util.*

class Graph(attributes: Attributes) {

    @set:Synchronized
    var attributes: Attributes = attributes
        set(value) {
            field = Attributes(value)
        }

    private val realNodes: MutableList<Node> = LinkedList()

    private var realInit: Node? = null

    constructor() : this(Attributes())

    @Synchronized
    fun setBooleanAttribute(name: String, value: Boolean) {
        attributes.setBoolean(name, value)
    }

    fun getBooleanAttribute(name: String): Boolean {
        return attributes.getBoolean(name)
    }

    val edgeCount: Int
        get() {
            var count = 0

            for (node in LinkedList(realNodes)) {
                count += node.outgoingEdgeCount
            }

            return count
        }

    var init: Node?
        get() {
            return this.realInit
        }
        set(value) {
            if (realNodes.contains(value)) {
                this.realInit = value
                number()
            }
        }

    @Synchronized
    fun setIntAttribute(name: String, value: Int) {
        attributes.setInt(name, value)
    }

    fun getIntAttribute(name: String): Int {
        return attributes.getInt(name)
    }

    fun getNode(id: Int): Node? {
        val i: Iterator<Node> = realNodes.iterator()
        while (i.hasNext()) {
            val n = i.next()
            if (n.id == id) {
                return n
            }
        }
        return null
    }

    val nodeCount: Int
        get() {
            return this.realNodes.size
        }

    val nodes: List<Node>
        get() {
            return LinkedList(realNodes)
        }

    @Synchronized
    fun setStringAttribute(name: String, value: String) {
        attributes.setString(name, value)
    }

    fun getStringAttribute(name: String): String? {
        return attributes.getString(name)
    }

    @Synchronized
    fun dfs(v: Visitor) {
        if (realInit == null) {
            return
        }
        forAllNodes(object : EmptyVisitor() {
            override fun visitNode(node: Node) {
                node.setBooleanAttribute("_reached", false)
            }
        })
        dfs(realInit!!, v)
        forAllNodes(object : EmptyVisitor() {
            override fun visitNode(node: Node) {
                node.setBooleanAttribute("_reached", false)
            }
        })
    }

    @Synchronized
    fun forAll(v: Visitor) {
        val i: Iterator<Node> = LinkedList(realNodes).iterator()
        while (i.hasNext()) {
            val n = i.next()
            v.visitNode(n)
            n.forAllEdges(v)
        }
    }

    @Synchronized
    fun forAllEdges(v: Visitor) {
        val i: Iterator<Node> = LinkedList(realNodes).iterator()
        while (i.hasNext()) {
            val n = i.next()
            n.forAllEdges(v)
        }
    }

    @Synchronized
    fun forAllNodes(v: Visitor) {
        val i: Iterator<Node> = LinkedList(realNodes).iterator()
        while (i.hasNext()) {
            val n = i.next()
            v.visitNode(n)
        }
    }

    @Synchronized
    fun save(format: Int) {
        save(System.out, format)
    }

    @Synchronized
    fun save() {
        save(System.out, SM_FORMAT)
    }

    @Synchronized
    @Throws(IOException::class)
    fun save(fname: String, format: Int) {
        save(PrintStream(FileOutputStream(fname)), format)
    }

    @Synchronized
    @Throws(IOException::class)
    fun save(fname: String) {
        save(PrintStream(FileOutputStream(fname)), SM_FORMAT)
    }

    @Synchronized
    internal fun addNode(n: Node) {
        realNodes.add(n)
        if (init == null) {
            init = n
        }
        number()
    }

    @Synchronized
    internal fun removeNode(n: Node) {
        realNodes.remove(n)
        if (init == n) {
            init = if (realNodes.size != 0) {
                realNodes[0]
            } else {
                null
            }
        }
        number()
    }

    @Synchronized
    private fun number() {
        var cnt: Int = if (realInit != null) {
            realInit!!.id = 0
            1
        } else {
            0
        }
        val i: Iterator<Node> = realNodes.iterator()
        while (i.hasNext()) {
            val n = i.next()
            if (n != init) {
                n.id = cnt++
            }
        }
    }

    @Synchronized
    private fun dfs(n: Node, v: Visitor) {
        if (n.getBooleanAttribute("_reached")) {
            return
        }
        n.setBooleanAttribute("_reached", true)
        v.visitNode(n)
        n.forAllEdges(object : EmptyVisitor() {
            override fun visitEdge(edge: Edge) {
                dfs(edge.next, v)
            }
        })
    }

    @Synchronized
    private fun save(out: PrintStream, format: Int) {
        when (format) {
            SM_FORMAT -> saveSm(out)
            FSP_FORMAT -> saveFsp(out)
            XML_FORMAT -> saveXml(out)
            SPIN_FORMAT -> saveSpin(out)
            else -> throw RuntimeException("Unknown format!")
        }
    }

    @Synchronized
    private fun saveFsp(out: PrintStream) {
        var empty = false
        if (init != null) {
            out.print("RES = S" + init!!.id)
        } else {
            out.print("Empty")
            empty = true
        }
        for (n in realNodes) {

            out.println(",")
            n.save(out, FSP_FORMAT)
        }

        out.println(".")
        val nsets = getIntAttribute("nsets")
        if (nsets == 0 && !empty) {
            var first = true

            out.print("AS = { ")
            val i: Iterator<Node> = realNodes.iterator()
            while (i.hasNext()) {
                val n = i.next()
                if (n.getBooleanAttribute("accepting")) {
                    if (!first) {
                        out.print(", ")
                    } else {
                        first = false
                    }

                    out.print("S" + n.id)
                }
            }

            out.println(" }")
        } else if (!empty) { // nsets != 0
            for (k in 0 until nsets) {
                var first = true

                out.print("AS$k = { ")
                val i: Iterator<Node> = realNodes.iterator()
                while (i.hasNext()) {
                    val n = i.next()
                    if (n.getBooleanAttribute("acc$k")) {
                        if (!first) {
                            out.print(", ")
                        } else {
                            first = false
                        }

                        out.print("S" + n.id)
                    }
                }

                out.println(" }")
            }
        }
        if (out !== System.out) {
            out.close()
        }
    }

    @Synchronized
    private fun saveSm(out: PrintStream) {
        out.println(realNodes.size)
        out.println(attributes)
        if (init != null) {
            init!!.save(out, SM_FORMAT)
        }
        val i: Iterator<Node> = realNodes.iterator()
        while (i.hasNext()) {
            val n = i.next()
            if (n != init) {
                n.save(out, SM_FORMAT)
            }
        }
    }

    // robbyjo's contribution
    @Synchronized
    private fun saveSpin(out: PrintStream) {
        if (init != null) {
            out.println("never {")
        } else {
            out.println("Empty")
            return
        }
        init!!.save(out, SPIN_FORMAT)
        val i: Iterator<Node> = realNodes.iterator()
        while (i.hasNext()) {
            val n = i.next()
            if (init == n) {
                continue
            }
            n.save(out, SPIN_FORMAT)
            out.println()
        }
        out.println("}")
    }

    @Synchronized
    private fun saveXml(out: PrintStream) {
        out.println("<?xml version=\"1.0\"?>")
        out.println("<graph nodes=\"" + realNodes.size + "\">")
        attributes.save(out, XML_FORMAT)
        val i: Iterator<Node> = realNodes.iterator()
        while (i.hasNext()) {
            val n = i.next()
            if (n != init) {
                n.save(out, XML_FORMAT)
            } else {
                n.setBooleanAttribute("init", true)
                n.save(out, XML_FORMAT)
                n.setBooleanAttribute("init", false)
            }
        }
        out.println("</graph>")
    }

    companion object {

        const val SM_FORMAT = 0

        const val FSP_FORMAT = 1

        const val XML_FORMAT = 2

        const val SPIN_FORMAT = 3

        @Throws(IOException::class)
        fun load(): Graph {
            return load(BufferedReader(InputStreamReader(System.`in`)))
        }

        @Throws(IOException::class)
        fun load(fileName: String): Graph {
            return load(BufferedReader(FileReader(fileName)))
        }

        @Throws(IOException::class)
        private fun load(reader: BufferedReader): Graph {
            val ns = readInt(reader)
            val nodes = arrayOfNulls<Node>(ns)
            val g = Graph(readAttributes(reader))
            for (i in 0 until ns) {
                val nt = readInt(reader)
                if (nodes[i] == null) {
                    nodes[i] = Node(g, readAttributes(reader))
                } else {
                    nodes[i]!!.attributes = readAttributes(reader)
                }
                for (j in 0 until nt) {
                    val nxt = readInt(reader)
                    val gu = readString(reader)
                    val ac = readString(reader)
                    if (nodes[nxt] == null) {
                        nodes[nxt] = Node(g)
                    }
                    Edge(nodes[i]!!, nodes[nxt]!!, gu, ac, readAttributes(reader))
                }
            }
            g.number()
            return g
        }

        @Throws(IOException::class)
        private fun readAttributes(reader: BufferedReader): Attributes {
            return Attributes(readLine(reader))
        }

        @Throws(IOException::class)
        private fun readInt(reader: BufferedReader): Int {
            return readLine(reader).toInt()
        }

        @Throws(IOException::class)
        private fun readLine(reader: BufferedReader): String {
            var line: String
            do {
                line = reader.readLine()
                val idx = line.indexOf('#')
                if (idx != -1) {
                    line = line.substring(0, idx)
                }
                line = line.trim { it <= ' ' }
            } while (line.isEmpty())
            return line
        }

        @Throws(IOException::class)
        private fun readString(reader: BufferedReader): String {
            return readLine(reader)
        }

    }

}