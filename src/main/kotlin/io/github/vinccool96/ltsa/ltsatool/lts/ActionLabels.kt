package io.github.vinccool96.ltsa.ltsatool.lts

import java.util.*


abstract class ActionLabels {

    var follower: ActionLabels? = null

    protected var locals: Hashtable<*, *>? = null

    protected var globals: Hashtable<*, *>? = null

    open fun initContext(var1: Hashtable<*, *>?, var2: Hashtable<*, *>?) {
        locals = var1
        globals = var2
        initialise()
        checkDuplicateVarDefn()
        follower?.initContext(var1, var2)
    }

    open fun clearContext() {
        removeVarDefn()
        if (follower != null) {
            follower!!.clearContext()
        }
    }

    open fun nextName(): String {
        var var1 = computeName()
        if (follower != null) {
            var1 = var1 + "." + follower!!.nextName()
            if (!follower!!.hasMoreNames()) {
                follower!!.initialise()
                next()
            }
        } else {
            next()
        }
        return var1
    }

    abstract fun hasMoreNames(): Boolean

    open fun getActions(var1: Hashtable<*, *>?, var2: Hashtable<*, *>?): Vector<String> {
        val names = Vector<String>()
        initContext(var1, var2)
        while (hasMoreNames()) {
            val var4 = nextName()
            names.addElement(var4)
        }
        clearContext()
        return names
    }

    open fun hasMultipleValues(): Boolean {
        return if (this !is ActionRange && this !is ActionSet && this !is ActionVarRange && this !is ActionVarSet) {
            follower?.hasMultipleValues() ?: false
        } else {
            true
        }
    }

    protected open fun checkDuplicateVarDefn() {}

    protected open fun removeVarDefn() {}

    protected abstract fun computeName(): String

    protected abstract operator fun next()

    protected abstract fun initialise()

    open fun myclone(): ActionLabels? {
        val var1 = make()
        if (follower != null) {
            var1.follower = follower!!.myclone()
        }
        return var1
    }

    protected abstract fun make(): ActionLabels

}