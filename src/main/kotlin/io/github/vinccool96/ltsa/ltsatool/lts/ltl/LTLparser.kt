package io.github.vinccool96.ltsa.ltsatool.lts.ltl

import io.github.vinccool96.ltsa.ltsatool.lts.Diagnostics
import io.github.vinccool96.ltsa.ltsatool.lts.Lex
import io.github.vinccool96.ltsa.ltsatool.lts.Symbol

class LTLparser(private var lex: Lex) {

    private var fac: FormulaFactory = FormulaFactory()

    private var current: Symbol? = null

    fun parse(): FormulaFactory {
        current = modify(lex.current)
        if (current == null) {
            nextSymbol()
        }
        fac.formula = ltlUnary()
        return fac
    }

    private fun nextSymbol(): Symbol? {
        return modify(lex.nextSymbol()).also { current = it }
    }

    private fun pushSymbol() {
        lex.pushSymbol()
    }

    private fun currentIs(var1: Int, var2: String) {
        if (current!!.kind != var1) {
            Diagnostics.fatal(var2, current)
        }
    }

    private fun modify(var1: Symbol?): Symbol? {
        return if (var1 == null) {
            null
        } else if (var1.kind != 123) {
            var1
        } else {
            val var2: Symbol
            if (var1.toString() == "X") {
                var2 = Symbol(var1)
                var2.kind = 23
                var2
            } else if (var1.toString() == "U") {
                var2 = Symbol(var1)
                var2.kind = 20
                var2
            } else {
                var1
            }
        }
    }

    private fun ltlUnary(): Formula? {
        val var1 = current
        return when (current!!.kind) {
            23, 45, 74, 75 -> {
                nextSymbol()
                fac.make(null as Formula?, var1!!, ltlUnary()!!)
            }
            53 -> {
                nextSymbol()
                val var3 = ltlOr()
                currentIs(54, ") expected to end LTL expression")
                nextSymbol()
                var3
            }
            123 -> {
                nextSymbol()
                if (!PredicateDefinition.contains(var1!!)) {
                    Diagnostics.fatal("proposition not defined $var1", var1 as Symbol?)
                }
                fac.make(var1)
            }
            else -> {
                Diagnostics.fatal("syntax error in LTL expression", current)
                null
            }
        }
    }

    private fun ltlAnd(): Formula? {
        var var1: Formula?
        var var2: Symbol?
        var var3: Formula?
        var1 = ltlUnary()
        while (current!!.kind == 42) {
            var2 = current
            nextSymbol()
            var3 = ltlUnary()
            var1 = fac.make(var1, var2!!, var3!!)
        }
        return var1
    }

    private fun ltlOr(): Formula? {
        var var1: Formula?
        var var2: Symbol?
        var var3: Formula?
        var1 = ltlBinary()
        while (current!!.kind == 40) {
            var2 = current
            nextSymbol()
            var3 = ltlBinary()
            var1 = fac.make(var1, var2!!, var3!!)
        }
        return var1
    }

    private fun ltlBinary(): Formula? {
        var var1 = ltlAnd()
        if (current!!.kind == 20 || current!!.kind == 69 || current!!.kind == 76) {
            val var2 = current
            nextSymbol()
            val var3 = ltlAnd()
            var1 = fac.make(var1, var2!!, var3!!)
        }
        return var1
    }

}