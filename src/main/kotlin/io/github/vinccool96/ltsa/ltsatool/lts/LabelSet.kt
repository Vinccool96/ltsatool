package io.github.vinccool96.ltsa.ltsatool.lts

import io.github.vinccool96.ltsa.ltsatool.lts.Diagnostics.fatal
import java.util.*


class LabelSet {

    var isConstant = false

    var labels: Vector<ActionLabels>? = null

    var actions: Vector<String>? = null

    constructor(symbol: Symbol, labels: Vector<ActionLabels>?) {
        this.labels = labels
        if (constants!!.put(symbol.toString(), this) != null) {
            fatal("duplicate set definition: $symbol", symbol as Symbol?)
        }
        actions = this.getActions(null)
        isConstant = true
        this.labels = null
    }

    constructor(labels: Vector<ActionLabels>?) {
        this.labels = labels
    }

    fun getActions(var1: Hashtable<String, Value>?): Vector<String>? {
        return this.getActions(null, var1)
    }

    fun getActions(locals: Hashtable<String, Value>?, globals: Hashtable<String, Value>?): Vector<String>? {
        return if (isConstant) {
            actions
        } else if (labels == null) {
            null
        } else {
            val names = Vector<String>()
            val var4 = Hashtable<String, String>()
            val var5 = if (locals != null) locals.clone() as Hashtable<String, Value> else null
            val elements = labels!!.elements()
            while (elements.hasMoreElements()) {
                val element = elements.nextElement() as ActionLabels
                element.initContext(var5, globals)
                while (element.hasMoreNames()) {
                    val name = element.nextName()
                    if (!var4.containsKey(name)) {
                        names.addElement(name)
                        var4[name] = name
                    }
                }
                element.clearContext()
            }
            names
        }
    }

    companion object {
        var constants: Hashtable<String, LabelSet>? = null
    }

}