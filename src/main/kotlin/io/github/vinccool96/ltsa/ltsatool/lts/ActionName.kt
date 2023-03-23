package io.github.vinccool96.ltsa.ltsatool.lts

class ActionName(protected var name: Symbol) : ActionLabels() {

    protected var consumed = true

    override fun computeName(): String {
        return name.toString()
    }

    override fun initialise() {
        consumed = false
    }

    override fun next() {
        consumed = true
    }

    override fun hasMoreNames(): Boolean {
        return !consumed
    }

    override fun make(): ActionLabels {
        return ActionName(name)
    }

}