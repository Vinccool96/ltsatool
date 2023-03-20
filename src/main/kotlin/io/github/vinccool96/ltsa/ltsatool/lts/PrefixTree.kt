package io.github.vinccool96.ltsa.ltsatool.lts

import java.util.*
import kotlin.math.max

class PrefixTree(val name: String?) {

    var value: Int? = 0

    var isInt = false

    var subname: PrefixTree? = null

    var list: PrefixTree? = null

    var lastprefix = false

    init {
        this.checkInt()
    }

    private fun add(var1: String, var2: Int) {
        val var3 = prefix(var1, var2)
        if (var3 != null) {
            if (var3 == name && !lastprefix) {
                val var4 = prefix(var1, var2 + 1)
                if (var4 == null) {
                    lastprefix = true
                    return
                }
                if (subname == null) {
                    subname = PrefixTree(var4)
                }
                subname!!.add(var1, var2 + 1)
            } else {
                if (list == null) {
                    list = PrefixTree(var3)
                }
                list!!.add(var1, var2)
            }
        }
    }

    val subLists: Array<PrefixTree?>
        get() {
            val var1 = Vector<PrefixTree?>()
            var var2 = this
            var var3 = list
            var1.addElement(this)
            while (var3 != null) {
                if (!equals(var2.subname, var3.subname) || var2.isInt != var3.isInt) {
                    var1.addElement(var3)
                    var2 = var3
                }
                var3 = var3.list
            }
            var1.addElement(null)
            val var4 = arrayOfNulls<PrefixTree>(var1.size)
            var1.copyInto(var4)
            return var4
        }

    fun checkInt() {
        try {
            value = name?.toInt()
            isInt = true
        } catch (_: NumberFormatException) {
        }
    }

    fun getStrings(var1: Vector<String>, var2: Int, var3: String?) {
        var var4: PrefixTree? = this
        while (var4 != null) {
            val var5 = if (var3 == null) {
                var4.item() ?: ""
            } else {
                var3 + dotted(var4.item() ?: "")
            }
            if (var4.subname == null) {
                var1.addElement(var5)
            } else if (var2 > 0) {
                var4.subname!!.getStrings(var1, var2 - 1, var5)
            } else {
                var1.addElement(var5 + dotted(var4.subname.toString()))
            }
            var4 = var4.list
        }
    }

    fun maxDepth(): Int {
        var var1: PrefixTree? = this
        var var2: Int
        var2 = 0
        while (var1 != null) {
            var2 = if (var1.subname == null) {
                max(var2, 1)
            } else {
                max(1 + var1.subname!!.maxDepth(), var2)
            }
            var1 = var1.list
        }
        return var2
    }

    fun item(): String? {
        return if (isInt) "[$name]" else name
    }

    override fun toString(): String {
        val var1 = subLists
        var var2: String
        var2 = if (var1.size > 2) {
            "{"
        } else {
            ""
        }
        for (var3 in 0 until var1.size - 1) {
            var2 = if (var3 < var1.size - 2) {
                var2 + listString(var1[var3]!!, var1[var3 + 1]!!) + ", "
            } else {
                var2 + listString(var1[var3]!!, var1[var3 + 1]!!)
            }
        }
        return if (var1.size > 2) {
            "$var2}"
        } else {
            var2
        }
    }

    companion object {

        fun addName(var0: PrefixTree?, var1: String): PrefixTree {
            if (var0 == null) {
                return PrefixTree(prefix(var1, 0))
            }
            var0.add(var1, 0)
            return var0
        }

        fun equals(var0: PrefixTree?, var1: PrefixTree?): Boolean {
            return if (var0 === var1) {
                true
            } else if (var0 != null && var1 != null) {
                if (var0.name != var1.name) {
                    false
                } else {
                    equals(var0.subname, var1.subname) && equals(var0.list, var1.list)
                }
            } else {
                false
            }
        }

        fun prefix(var0: String, var1: Int): String? {
            var var2 = 0
            var var3 = 0
            while (var3 < var1) {
                var2 = var0.indexOf(46.toChar(), var2)
                if (var2 < 0) {
                    return null
                }
                ++var2
                ++var3
            }
            var3 = var0.indexOf(46.toChar(), var2)
            return if (var3 < 0) {
                var0.substring(var2)
            } else {
                var0.substring(var2, var3)
            }
        }

        fun listString(var0: PrefixTree, var1: PrefixTree): String {
            var var2: String
            if (var0.list == var1) {
                var2 = var0.item() ?: ""
            } else if (intRange(var0, var1)) {
                var2 = rangeString(var0, var1)
            } else {
                var2 = "{" + var0.item()
                var var3 = var0.list
                while (var3 !== var1) {
                    var2 = var2 + ", " + var3!!.item()
                    var3 = var3.list
                }
                var2 = "$var2}"
            }
            return if (var0.subname != null) var2 + dotted(var0.subname.toString()) else var2
        }

        private fun dotted(var0: String): String {
            return if (var0[0] == '[') var0 else ".$var0"
        }

        fun intRange(var0: PrefixTree?, var1: PrefixTree): Boolean {
            var var2 = var0
            while (var2 !== var1) {
                if (!var2!!.isInt) {
                    return false
                }
                var2 = var2.list
            }
            return true
        }

        fun rangeString(var0: PrefixTree?, var1: PrefixTree): String {
            var var2 = var0
            var var3: Int = 0
            while (var2 !== var1) {
                var2 = var2!!.list
                ++var3
            }
            val var4 = IntArray(var3)
            var2 = var0
            var var5 = 0
            while (var5 < var4.size) {
                var4[var5] = var2!!.value!!
                var2 = var2.list
                ++var5
            }
            sort(var4)
            return if (isOneRange(var4)) {
                "[" + var4[0] + ".." + var4[var4.size - 1] + "]"
            } else {
                var5 = 0
                var var6 = "{"
                while (var5 < var4.size) {
                    var var7 = var5
                    while (var7 < var4.size - 1 && var4[var7 + 1] - var4[var7] == 1) {
                        ++var7
                    }
                    var6 = if (var7 == var5) {
                        var6 + "[" + var4[var5] + "]"
                    } else {
                        var6 + "[" + var4[var5] + ".." + var4[var7] + "]"
                    }
                    var5 = var7 + 1
                    if (var5 < var4.size) {
                        var6 = "$var6, "
                    }
                }
                var6 = "$var6}"
                var6
            }
        }

        private fun isOneRange(var0: IntArray): Boolean {
            for (var1 in 0 until var0.size - 1) {
                if (var0[var1 + 1] - var0[var1] != 1) {
                    return false
                }
            }
            return true
        }

        private fun sort(var0: IntArray) {
            for (var2 in 0 until var0.size - 1) {
                var var1 = var2
                var var3: Int = var2 + 1
                while (var3 < var0.size) {
                    if (var0[var3] < var0[var1]) {
                        var1 = var3
                    }
                    ++var3
                }
                var3 = var0[var2]
                var0[var2] = var0[var1]
                var0[var1] = var3
            }
        }

    }

}