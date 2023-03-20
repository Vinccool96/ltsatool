package io.github.vinccool96.ltsa.ltsatool.lts

import java.util.*

class Relation : Hashtable<Any, Any>() {

    var isRelation = false
        private set

    override fun put(key: Any, value: Any): Any? {
        return if (!this.containsKey(key)) {
            super.put(key, value)
        } else {
            if (!isRelation) {
                isRelation = true
            }
            val var3: Any? = this[key]
            val var4: Vector<Any>
            if (var3 is Vector<*>) {
                var4 = var3 as Vector<Any>
                if (!var4.contains(value)) {
                    var4.addElement(value)
                }
            } else {
                var4 = Vector(4)
                var4.addElement(var3)
                if (var4 != var3) {
                    var4.addElement(value)
                }
                super.put(key, var4)
            }
            var3
        }
    }

    fun inverse(): Relation {
        val var1 = Relation()
        val var2: Enumeration<*> = keys()
        while (true) {
            while (var2.hasMoreElements()) {
                val var3 = var2.nextElement()
                val var4: Any? = this[var3]
                if (var4 !is Vector<*>) {
                    var1[var4] = var3
                } else {
                    val var5 = var4.elements()
                    while (var5.hasMoreElements()) {
                        var1[var5.nextElement()] = var3
                    }
                }
            }
            return var1
        }
    }

    fun union(var1: Relation?) {
        if (var1 != null) {
            val var2: Enumeration<*> = var1.keys()
            while (var2.hasMoreElements()) {
                val var3 = var2.nextElement()
                val var4: Any? = var1[var3]
                putValues(var3, var4)
            }
        }
    }

    fun relabel(var1: Relation) {
        val var2 = keys()
        while (true) {
            while (true) {
                while (var2.hasMoreElements()) {
                    val var3 = var2.nextElement() as String
                    val var4: Any? = this[var3]
                    var var5: Any?
                    var var6: Enumeration<*>
                    if (var1.containsKey(var3)) {
                        var5 = var1[var3]
                        this.remove(var3)
                        if (var5 !is Vector<*>) {
                            putValues(var5, var4)
                        } else {
                            var6 = var5.elements()
                            while (var6.hasMoreElements()) {
                                putValues(var6.nextElement(), var4)
                            }
                        }
                    } else if (hasPrefix(var3, var1)) {
                        var5 = var1[prefix(var3, var1)]
                        if (var5 !is Vector<*>) {
                            val var8: String = prefixReplace(var3, var5 as String, var1)
                            putValues(var8, var4)
                        } else {
                            var6 = var5.elements()
                            while (var6.hasMoreElements()) {
                                val var7: String = prefixReplace(var3, var6.nextElement() as String, var1)
                                putValues(var7, var4)
                            }
                        }
                    }
                }
                return
            }
        }
    }

    protected fun putValues(var1: Any?, var2: Any?) {
        if (var2 !is Vector<*>) {
            this[var1] = var2
        } else {
            val var3 = var2.elements()
            while (var3.hasMoreElements()) {
                this[var1] = var3.nextElement()
            }
        }
    }

    companion object {

        private fun prefixReplace(var0: String, var1: String, var2: Hashtable<Any, Any>): String {
            val var3 = maximalPrefix(var0, var2)
            return if (var3 < 0) var0 else var1 + var0.substring(var3)
        }

        private fun maximalPrefix(var0: String, var1: Hashtable<Any, Any>): Int {
            val var2 = var0.lastIndexOf(46.toChar())
            return if (var2 < 0) {
                var2
            } else {
                if (var1.containsKey(var0.substring(0, var2))) var2 else maximalPrefix(var0.substring(0, var2), var1)
            }
        }

        private fun hasPrefix(var0: String, var1: Hashtable<Any, Any>): Boolean {
            return maximalPrefix(var0, var1) >= 0
        }

        private fun prefix(var0: String, var1: Hashtable<Any, Any>): String {
            return var0.substring(0, maximalPrefix(var0, var1))
        }

    }

}