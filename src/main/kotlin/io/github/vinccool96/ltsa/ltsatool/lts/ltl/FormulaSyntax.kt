package io.github.vinccool96.ltsa.ltsatool.lts.ltl

import io.github.vinccool96.ltsa.ltsatool.lts.*
import java.util.*

class FormulaSyntax private constructor(var left: FormulaSyntax?, var operator: Symbol?, var right: FormulaSyntax?,
        var proposition: Symbol?, var range: ActionLabels?, var action: ActionLabels?, val parameters: Vector<Any>?) {

    fun expand(var1: FormulaFactory, var2: Hashtable<String, Value>, var3: Hashtable<String, Value>): Formula? {
        return if (proposition == null) {
            if (action != null) {
                var1.make(action!!, var2, var3)
            } else if (operator!!.kind == 25) {
                var1.make(parameters as Stack<Symbol>, var2, var3)
            } else if (operator != null && range == null) {
                if (left == null) var1.make(null, operator!!, right!!.expand(var1, var2, var3)!!) else var1.make(
                        left!!.expand(var1, var2, var3)!!, operator!!, right!!.expand(var1, var2, var3)!!)
            } else if (range != null && right != null) {
                range!!.initContext(var2, var3)
                var var8: Formula? = null
                while (range!!.hasMoreNames()) {
                    range!!.nextName()
                    var8 = if (var8 == null) {
                        right!!.expand(var1, var2, var3)
                    } else if (operator!!.kind == 42) {
                        var1.makeAnd(var8, right!!.expand(var1, var2, var3)!!)
                    } else {
                        var1.makeOr(var8, right!!.expand(var1, var2, var3)!!)
                    }
                }
                range!!.clearContext()
                var8
            } else {
                null
            }
        } else if (range != null) {
            var1.make(proposition!!, range!!, var2, var3)
        } else if (PredicateDefinition.definitionsDelegate.isInitialized() && PredicateDefinition.definitions.containsKey(
                        proposition.toString())) {
            var1.make(proposition!!)
        } else {
            val var4 = AssertDefinition.definitions?.get(proposition.toString())
            if (var4 == null) {
                Diagnostics.fatal("LTL fluent or assertion not defined: $proposition", proposition)
                return null
            }
            if (parameters == null) {
                var4.ltlFormula.expand(var1, var2, var4.initParams)
            } else {
                if (parameters.size != var4.params.size) {
                    Diagnostics.fatal("Actual parameters do not match formals: $proposition", proposition)
                }
                val var5 = Hashtable<String, Value>()
                val var6 = paramValues(parameters as Vector<Stack<Symbol>>, var2, var3)
                for (var7 in parameters.indices) {
                    var5[var4.params.elementAt(var7)] = var6!!.elementAt(var7)
                }
                var4.ltlFormula.expand(var1, var2, var5)
            }
        }
    }

    private fun paramValues(var1: Vector<Stack<Symbol>>?, var2: Hashtable<String, Value>,
            var3: Hashtable<String, Value>): Vector<Value>? {
        return if (var1 == null) {
            null
        } else {
            val var4 = var1.elements()
            val var5 = Vector<Value>()
            while (var4.hasMoreElements()) {
                val var6 = var4.nextElement()
                var5.addElement(Expression.getValue(var6, var2, var3))
            }
            var5
        }
    }

    companion object {

        fun make(var0: FormulaSyntax, var1: Symbol, var2: FormulaSyntax): FormulaSyntax {
            return FormulaSyntax(var0, var1, var2, null, null, null, null)
        }

        fun make(var0: Symbol): FormulaSyntax {
            return FormulaSyntax(null, null, null, var0, null, null, null)
        }

        fun make(var0: Symbol, var1: ActionLabels): FormulaSyntax {
            return FormulaSyntax(null, null, null, var0, var1, null, null)
        }

        fun make(var0: Symbol, var1: Vector<Any>): FormulaSyntax {
            return FormulaSyntax(null, null, null, var0, null, null, var1)
        }

        fun makeE(var0: Symbol, var1: Stack<Any>): FormulaSyntax {
            return FormulaSyntax(null, var0, null, null, null, null, var1)
        }

        fun make(var0: ActionLabels): FormulaSyntax {
            return FormulaSyntax(null, null, null, null, null, var0, null)
        }

        fun make(var0: Symbol, var1: ActionLabels, var2: FormulaSyntax): FormulaSyntax {
            return FormulaSyntax(null, var0, var2, null, var1, null, null)
        }

    }

}