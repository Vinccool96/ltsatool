package gov.nasa.ltl.graph

import java.io.PrintStream
import java.util.*

class Attributes {

    private var ht: Hashtable<Any, String>

    constructor() {
        ht = Hashtable()
    }

    constructor(a: Attributes) {
        ht = Hashtable()
        val e = a.ht.keys()
        while (e.hasMoreElements()) {
            val key = e.nextElement()
            ht[key] = a.ht[key]
        }
    }

    constructor(s: String) {
        ht = Hashtable()
        if (s == "-") {
            return
        }
        val st = StringTokenizer(s, ",")
        while (st.hasMoreTokens()) {
            val e = st.nextToken()
            val idx = e.indexOf("=")
            var key: String?
            var value: String
            if (idx == -1) {
                key = e
                value = ""
            } else {
                key = e.substring(0, idx)
                value = e.substring(idx + 1)
            }
            ht[key] = value
        }
    }

    fun setBoolean(name: String, value: Boolean) {
        if (value) {
            ht[name] = ""
        } else {
            ht.remove(name)
        }
    }

    fun getBoolean(name: String): Boolean {
        return ht[name] != null
    }

    fun setInt(name: String, value: Int) {
        ht[name] = value.toString()
    }

    fun getInt(name: String): Int {
        val o = ht[name] ?: return 0
        return try {
            o.toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }

    fun setString(name: String, value: String) {
        ht[name] = value
    }

    fun getString(name: String): String? {
        return ht[name]
    }

    @Synchronized
    fun save(out: PrintStream, format: Int) {
        when (format) {
            Graph.XML_FORMAT -> saveXml(out)
        }
    }

    override fun toString(): String {
        if (ht.size == 0) {
            return "-"
        }
        val sb = StringBuilder()
        val e = ht.keys()
        while (e.hasMoreElements()) {
            val key = e.nextElement()
            val value = ht[key]
            sb.append(key)
            if (value != "") {
                sb.append('=')
                sb.append(value)
            }
            if (e.hasMoreElements()) {
                sb.append(',')
            }
        }
        return sb.toString()
    }

    fun unset(name: String?) {
        ht.remove(name)
    }

    @Synchronized
    private fun saveXml(out: PrintStream) {
        if (ht.size == 0) {
            return
        }
        val e = ht.keys()
        while (e.hasMoreElements()) {
            val key = e.nextElement() as String
            val value = ht[key]
            if (value === "") {
                out.println("<$key/>")
            } else {
                out.println("<$key>$value</$key>")
            }
        }
    }

}