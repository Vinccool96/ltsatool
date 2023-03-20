package io.github.vinccool96.ltsa.ltsatool.lts

class ActionVarSet(protected val symbol: Symbol, set: LabelSet) : ActionSet(set) {

    override fun computeName(): String {
        val var1 = actions!!.elementAt(current)
        if (locals != null) {
            locals!![this.symbol.toString()] = Value(var1)
        }
        return var1
    }

    override fun checkDuplicateVarDefn() {
        if (locals != null) {
            if (locals!![this.symbol.toString()] != null) {
                Diagnostics.fatal("Duplicate variable definition: " + this.symbol, this.symbol as Symbol?)
            }
        }
    }

    override fun removeVarDefn() {
        if (locals != null) {
            locals!!.remove(this.symbol.toString())
        }
    }

    override fun make(): ActionLabels {
        return ActionVarSet(this.symbol, set)
    }

}