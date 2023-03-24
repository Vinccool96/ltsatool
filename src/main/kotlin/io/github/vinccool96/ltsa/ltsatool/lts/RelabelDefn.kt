package io.github.vinccool96.ltsa.ltsatool.lts

import java.util.*


class RelabelDefn {

    var newlabel: ActionLabels? = null

    var oldlabel: ActionLabels? = null

    var range: ActionLabels? = null

    var defns: Vector<RelabelDefn>? = null

    fun makeRelabels(var1: Hashtable<String, Value>, var2: Relation) {
        val var3 = Hashtable<String, Value>()
        mkRelabels(var1, var3, var2)
    }

    fun makeRelabels(var1: Hashtable<String, Value>, var2: Hashtable<String, Value>, var3: Relation) {
        mkRelabels(var1, var2, var3)
    }

    private fun mkRelabels(var1: Hashtable<String, Value>, var2: Hashtable<String, Value>, var3: Relation) {
        if (range != null) {
            range!!.initContext(var2, var1)
            while (range!!.hasMoreNames()) {
                range!!.nextName()
                val var4 = defns!!.elements()
                while (var4.hasMoreElements()) {
                    val var5 = var4.nextElement()
                    var5.mkRelabels(var1, var2, var3)
                }
            }
            range!!.clearContext()
        } else {
            newlabel!!.initContext(var2, var1)
            while (newlabel!!.hasMoreNames()) {
                val var7 = newlabel!!.nextName()
                oldlabel!!.initContext(var2, var1)
                while (oldlabel!!.hasMoreNames()) {
                    val var6 = oldlabel!!.nextName()
                    var3[var6] = var7
                }
            }
            newlabel!!.clearContext()
        }
    }

    companion object {

        fun getRelabels(var0: Vector<RelabelDefn>?, var1: Hashtable<String, Value>,
                var2: Hashtable<String, Value>): Relation? {
            return if (var0 == null) {
                null
            } else {
                val var3 = Relation()
                val var4 = var0.elements()
                while (var4.hasMoreElements()) {
                    val var5 = var4.nextElement()
                    var5.makeRelabels(var1, var2, var3)
                }
                var3
            }
        }

        fun getRelabels(var0: Vector<RelabelDefn>?): Relation? {
            return getRelabels(var0, Hashtable(), Hashtable())
        }

    }

}