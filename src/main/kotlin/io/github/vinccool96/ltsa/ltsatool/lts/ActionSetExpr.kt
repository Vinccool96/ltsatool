package io.github.vinccool96.ltsa.ltsatool.lts

import java.util.*

class ActionSetExpr(protected var left: LabelSet, protected var right: LabelSet) : ActionLabels() {

    protected var actions: Vector<String>? = null

    protected var current = 0

    protected var high = 0

    protected var low = 0

    override fun computeName(): String {
        return actions!!.elementAt(current)
    }

    override fun initialise() {
        val var1 = left.getActions(this.locals, this.globals)!!
        val var2 = right.getActions(this.locals, this.globals)!!
        actions = Vector()
        val var3 = var1.elements()
        while (var3.hasMoreElements()) {
            val var4 = var3.nextElement()
            if (!var2.contains(var4)) {
                actions!!.addElement(var4)
            }
        }
        low = 0
        current = low
        high = actions!!.size - 1
    }

    override operator fun next() {
        ++current
    }

    override fun hasMoreNames(): Boolean {
        return current <= high
    }

    override fun make(): ActionLabels {
        return ActionSetExpr(left, right)
    }

}