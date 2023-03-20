package io.github.vinccool96.ltsa.ltsatool.lts

import java.util.*

class ActionVarRange : ActionRange {

    protected val symbol: Symbol

    constructor(symbol: Symbol, rlow: Stack<Symbol>, rhigh: Stack<Symbol>) : super(rlow, rhigh) {
        this.symbol = symbol
    }

    constructor(symbol: Symbol, range: Range) : super(range) {
        this.symbol = symbol
    }

    override fun computeName(): String {
        if (locals != null) {
            locals!![this.symbol.toString()] = Value(current)
        }
        return current.toString()
    }

    override fun checkDuplicateVarDefn() {
        if (locals != null) {
            if (locals!![this.symbol.toString()] != null) {
                Diagnostics.fatal("Duplicate variable definition: ${this.symbol}", this.symbol as Symbol?)
            }
        }
    }

    override fun removeVarDefn() {
        if (locals != null) {
            locals!!.remove(this.symbol.toString())
        }
    }

    override fun make(): ActionLabels {
        return ActionVarRange(this.symbol, rlow, rhigh)
    }

}