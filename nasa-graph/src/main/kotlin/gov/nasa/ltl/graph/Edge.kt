package gov.nasa.ltl.graph

import java.io.PrintStream
import java.util.*

class Edge(val source: Node, val next: Node, val guard: String, val action: String, attributes: Attributes) {

    @set:Synchronized
    var attributes: Attributes = attributes
        set(value) {
            field = Attributes(value)
        }

    constructor(source: Node, next: Node, guard: String, action: String) : this(source, next, guard, action,
            Attributes())

    constructor(source: Node, next: Node, guard: String) : this(source, next, guard, "-", Attributes())

    constructor(source: Node, next: Node) : this(source, next, "-", "-", Attributes())

    constructor(source: Node, nextEdge: Edge) : this(source, nextEdge.next, nextEdge.guard, nextEdge.action,
            Attributes(nextEdge.attributes))

    constructor(sourceEdge: Edge, next: Node) : this(sourceEdge.source, next, sourceEdge.guard, sourceEdge.action,
            Attributes(sourceEdge.attributes))

    init {
        source.addOutgoingEdge(this)
        next.addIncomingEdge(this)
    }

    fun setBooleanAttribute(name: String, value: Boolean) {
        attributes.setBoolean(name, value)
    }

    fun getBooleanAttribute(name: String): Boolean {
        return attributes.getBoolean(name)
    }

    fun setIntAttribute(name: String, value: Int) {
        attributes.setInt(name, value)
    }

    fun getIntAttribute(name: String): Int {
        return attributes.getInt(name)
    }

    fun setStringAttribute(name: String, value: String) {
        attributes.setString(name, value)
    }

    fun getStringAttribute(name: String): String? {
        return attributes.getString(name)
    }

    @Synchronized
    fun remove() {
        source.removeOutgoingEdge(this)
        next.removeIncomingEdge(this)
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
        var accs = ""
        val g = if (guard == "-") {
            "TRUE"
        } else {
            guard
        }
        val nsets: Int = source.graph.getIntAttribute("nsets")
        if (nsets == 0) {
            if (getBooleanAttribute("accepting")) {
                accs = "@"
            }
        } else {
            var first = true
            val sb = StringBuilder()
            for (i in 0 until nsets) {
                if (getBooleanAttribute("acc$i")) {
                    if (first) {
                        first = false
                    } else {
                        sb.append(',')
                    }
                    sb.append(i)
                }
            }
            if (!first) {
                sb.insert(0, '{')
                sb.append('}')
                accs = sb.toString()
            }
        }


        out.print(g + accs + "-> S" + next.id)
    }

    private fun saveSm(out: PrintStream) {
        out.print("    ")
        out.println(next.id)
        out.print("    ")
        out.println(guard)
        out.print("    ")
        out.println(action)
        out.print("    ")
        out.println(attributes)
    }

    private fun saveSpin(out: PrintStream) {
        var g = if (guard == "-") "1" else guard
        var accs = ""
        var tok = StringTokenizer(g, "&")
        g = ""
        while (tok.hasMoreTokens()) {
            g += tok.nextToken()
            if (tok.hasMoreTokens()) {
                g += " && "
            }
        }
        tok = StringTokenizer(g, "|")
        g = ""
        while (tok.hasMoreTokens()) {
            g += tok.nextToken()
            if (tok.hasMoreTokens()) {
                g += " || "
            }
        }
        val nsets: Int = source.graph.getIntAttribute("nsets")
        if (nsets == 0) {
            if (getBooleanAttribute("accepting")) {
                accs = "@"
            }
        } else {
            var first = true
            val sb = StringBuilder()
            for (i in 0 until nsets) {
                if (getBooleanAttribute("acc$i")) {
                    if (first) {
                        first = false
                    } else {
                        sb.append(',')
                    }
                    sb.append(i)
                }
            }
            if (!first) {
                sb.insert(0, '{')
                sb.append('}')
                accs = sb.toString()
            }
        }
        out.print("($g) $accs-> goto ")
        if (next.getBooleanAttribute("accepting")) {
            out.print("accept_")
        }
        out.print("S" + next.id)
    }

    private fun saveXml(out: PrintStream) {
        out.println("<transition to=\"" + next.id + "\">")
        if (guard != "-") {
            out.println("<guard>" + xmlQuote(guard) + "</guard>")
        }
        if (action != "-") {
            out.println("<action>" + xmlQuote(action) + "</action>")
        }
        attributes.save(out, Graph.XML_FORMAT)
        out.println("</transition>")
    }

    private fun xmlQuote(s: String): String {
        val sb = StringBuilder()
        for (element in s) {
            when (element) {
                '&' -> sb.append("&amp;")
                '<' -> sb.append("&lt;")
                '>' -> sb.append("&gt;")
                else -> sb.append(element)
            }
        }
        return sb.toString()
    }

}