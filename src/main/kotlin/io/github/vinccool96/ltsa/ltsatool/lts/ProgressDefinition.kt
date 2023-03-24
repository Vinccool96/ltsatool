package io.github.vinccool96.ltsa.ltsatool.lts

import java.util.*


class ProgressDefinition {

    var name: Symbol? = null

    var pactions: ActionLabels? = null

    var cactions: ActionLabels? = null

    var range: ActionLabels? = null

    fun makeProgressTest() {
        var var1: Vector<String>?
        var var2: Vector<String>? = null
        val var3 = name.toString()
        if (range == null) {
            var1 = pactions!!.getActions(null, null)
            if (cactions != null) {
                var2 = cactions!!.getActions(null, null)
            }
            ProgressTest(var3, var1, var2)
        } else {
            val var4 = Hashtable<String, Value>()
            range!!.initContext(var4, null)
            var var5: String
            while (range!!.hasMoreNames()) {
                var5 = range!!.nextName()
                var1 = pactions!!.getActions(var4, null)
                if (cactions != null) {
                    var2 = cactions!!.getActions(var4, null)
                }
                ProgressTest("$var3.$var5", var1, var2)
            }
            range!!.clearContext()
        }
    }

    companion object {

        var definitions: Hashtable<String, ProgressDefinition>? = null

        fun compile() {
            ProgressTest.init()
            val var0 = definitions!!.elements()
            while (var0.hasMoreElements()) {
                val var1 = var0.nextElement()
                var1.makeProgressTest()
            }
        }

    }

}