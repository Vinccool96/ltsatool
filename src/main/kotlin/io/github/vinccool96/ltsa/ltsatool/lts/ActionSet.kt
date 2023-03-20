package io.github.vinccool96.ltsa.ltsatool.lts

import java.util.*

open class ActionSet(val set: LabelSet) : ActionLabels() {

    protected var actions: Vector<String>? = null

    protected var current = 0

    protected var high = 0

    protected var low = 0

    override fun computeName(): String {
        return actions!!.elementAt(current)
    }

    override fun initialise() {
        actions = set.getActions(this.locals, this.globals)
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
        return ActionSet(set)
    }

}