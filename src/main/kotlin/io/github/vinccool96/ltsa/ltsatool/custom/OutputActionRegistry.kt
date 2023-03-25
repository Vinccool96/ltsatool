package io.github.vinccool96.ltsa.ltsatool.custom

import io.github.vinccool96.ltsa.ltsatool.lts.Relation
import java.util.*

class OutputActionRegistry(var actionMap: Relation, var msg: AnimationMessage) {

    var outputs = Hashtable<String, Vector<AnimationAction>>()

    fun register(var1: String, var2: AnimationAction) {
        var var3 = outputs[var1]
        if (var3 != null) {
            var3.addElement(var2)
        } else {
            var3 = Vector()
            var3.addElement(var2)
            outputs[var1] = var3
        }
    }

    fun doAction(var1: String) {
        msg.traceMsg(var1)
        val var2 = actionMap[var1]
        if (var2 != null) {
            if (var2 is String) {
                execute(var2)
            } else {
                val var3 = var2 as Vector<String>
                val var4 = var3.elements()
                while (var4.hasMoreElements()) {
                    execute(var4.nextElement())
                }
            }
        }
    }

    private fun execute(var1: String) {
        msg.debugMsg("-action -$var1")
        val var2 = outputs[var1]
        if (var2 != null) {
            val var3 = var2.elements()
            while (var3.hasMoreElements()) {
                val var4 = var3.nextElement()
                var4.action()
            }
        }
    }

}