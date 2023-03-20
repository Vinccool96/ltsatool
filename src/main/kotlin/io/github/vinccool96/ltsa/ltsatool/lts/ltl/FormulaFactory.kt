package io.github.vinccool96.ltsa.ltsatool.lts.ltl

import io.github.vinccool96.ltsa.ltsatool.lts.*
import java.util.*

class FormulaFactory {

    var nv = NotVisitor(this)

    var id = 1

    var subf: MutableMap<String, Formula> = HashMap()

    val props: SortedSet<Proposition> = TreeSet()

    var actionPredicates: Hashtable<String, Vector<String>>? = null

    private var hasNext = false

    fun nextInFormula(): Boolean {
        return hasNext
    }

    var formula: Formula? = null
        set(value) {
            field = makeNot(value!!)
        }

    fun make(var1: Symbol): Formula {
        return unique(Proposition(var1))
    }

    fun make(var1: Symbol, var2: ActionLabels, var3: Hashtable<String, Value>?,
            var4: Hashtable<String, Value>?): Formula? {
        var2.initContext(var3, var4)
        var var5: Formula? = null
        while (var2.hasMoreNames()) {
            val var6: String = var2.nextName()
            val var7 = Symbol(var1, "$var1.$var6")
            var5 = if (var5 == null) {
                this.make(var7)
            } else {
                makeOr(var5, this.make(var7))
            }
        }
        var2.clearContext()
        return var5
    }

    fun make(var1: Stack<Symbol>, var2: Hashtable<String, Value>?, var3: Hashtable<String, Value>?): Formula {
        return (if (Expression.evaluate(var1, var2, var3) > 0) True.make() else False.make())
    }

    fun make(var1: ActionLabels, var2: Hashtable<String, Value>?, var3: Hashtable<String, Value>?): Formula {
        if (actionPredicates == null) {
            actionPredicates = Hashtable()
        }
        val var4: Vector<String> = var1.getActions(var2, var3)
        val var5: String = Alphabet(var4).toString()
        if (!actionPredicates!!.containsKey(var5)) {
            actionPredicates!![var5] = var4
        }
        return unique(Proposition(Symbol(123, var5)))
    }

    fun makeTick(): Formula {
        if (actionPredicates == null) {
            actionPredicates = Hashtable()
        }
        val var1 = Vector<String>(1)
        var1.add("tick")
        val var2: String = Alphabet(var1).toString()
        if (!actionPredicates!!.containsKey(var2)) {
            actionPredicates!![var2] = var1
        }
        return unique(Proposition(Symbol(123, var2)))
    }

    fun make(formula: Formula, symbol: Symbol, otherFormula: Formula): Formula? {
        return when (symbol.kind) {
            Symbol.UNTIL -> {
                if (normalLTL) {
                    makeUntil(formula, otherFormula)
                } else makeUntil(makeImplies(makeTick(), formula), makeAnd(makeTick(), otherFormula))
            }
            Symbol.NEXTTIME -> {
                if (normalLTL) {
                    makeNext(otherFormula)
                } else makeNext(makeWeakUntil(this.makeNot(makeTick()), makeAnd(makeTick(), otherFormula)))
            }
            Symbol.OR -> makeOr(formula, otherFormula)
            Symbol.AND -> makeAnd(formula, otherFormula)
            Symbol.PLING -> this.makeNot(otherFormula)
            Symbol.ARROW -> makeImplies(formula, otherFormula)
            Symbol.EVENTUALLY -> {
                if (normalLTL) {
                    makeEventually(otherFormula)
                } else makeEventually(makeAnd(makeTick(), otherFormula))
            }
            Symbol.ALWAYS -> {
                if (normalLTL) {
                    makeAlways(otherFormula)
                } else makeAlways(makeImplies(makeTick(), otherFormula))
            }
            Symbol.EQUIVALENT -> makeEquivalent(formula, otherFormula)
            Symbol.WEAKUNTIL -> {
                if (normalLTL) {
                    makeWeakUntil(formula, otherFormula)
                } else makeWeakUntil(makeImplies(makeTick(), formula), makeAnd(makeTick(), otherFormula))
            }
            else -> {
                Diagnostics.fatal("Unexpected operator in LTL expression: $symbol", symbol as Symbol?)
                null
            }
        }
    }

    fun makeAnd(var1: Formula, var2: Formula): Formula {
        return if (var1 === var2) {
            var1
        } else if (var1 !== False.make() && var2 !== False.make()) {
            if (var1 === True.make()) {
                var2
            } else if (var2 === True.make()) {
                var1
            } else if (var1 === this.makeNot(var2)) {
                False.make()
            } else if (var1 is Next && var2 is Next) {
                makeNext(makeAnd(var1.next, var2.next))
            } else {
                if (var1 < var2) unique(And(var1, var2)) else unique(And(var2, var1))
            }
        } else {
            False.make()
        }
    }

    fun makeOr(var1: Formula, var2: Formula): Formula {
        return if (var1 === var2) {
            var1
        } else if (var1 !== True.make() && var2 !== True.make()) {
            if (var1 === False.make()) {
                var2
            } else if (var2 === False.make()) {
                var1
            } else if (var1 === this.makeNot(var2)) {
                True.make()
            } else {
                if (var1 < var2) unique(Or(var1, var2)) else unique(Or(var2, var1))
            }
        } else {
            True.make()
        }
    }

    fun makeUntil(var1: Formula, var2: Formula): Formula {
        return if (var2 === False.make()) {
            False.make()
        } else {
            if (var1 is Next && var2 is Next) makeNext(makeUntil(var1.next, var2.next)) else unique(Until(var1, var2))
        }
    }

    fun makeWeakUntil(var1: Formula, var2: Formula): Formula {
        return makeRelease(var2, makeOr(var1, var2))
    }

    fun makeRelease(var1: Formula, var2: Formula): Formula {
        return unique(Release(var1, var2))
    }

    fun makeImplies(var1: Formula, var2: Formula): Formula {
        return makeOr(this.makeNot(var1), var2)
    }

    fun makeEquivalent(var1: Formula, var2: Formula): Formula {
        return makeAnd(makeImplies(var1, var2), makeImplies(var2, var1))
    }

    fun makeEventually(var1: Formula): Formula {
        return makeUntil(True.make(), var1)
    }

    fun makeAlways(var1: Formula): Formula {
        return makeRelease(False.make(), var1)
    }

    fun makeNot(var1: Formula): Formula {
        return var1.accept(nv)!!
    }

    fun makeNot(var1: Proposition): Formula {
        return unique(Not(var1))
    }

    fun makeNext(var1: Formula): Formula {
        hasNext = true
        return unique(Next(var1))
    }

    fun processUntils(var1: Formula, var2: MutableList<Until>): Int {
        var1.accept(UntilVisitor(this, var2))
        return var2.size
    }

    fun specialCaseV(var1: Formula, var2: Set<*>): Boolean {
        val var3 = makeRelease(False.make(), var1)
        return var2.contains(var3)
    }

    fun syntaxImplied(formula: Formula?, var2: SortedSet<Formula>, var3: SortedSet<Formula>): Boolean {
        return if (formula == null) {
            true
        } else if (formula is True) {
            true
        } else if (var2.contains(formula)) {
            true
        } else if (formula.isLiteral) {
            false
        } else {
            val sub1Formula = formula.getSub1()
            val sub2Formula = formula.getSub2()
            val var6 = if (formula !is Until && formula !is Release) null else formula
            val var7 = syntaxImplied(sub2Formula, var2, var3)
            val var8 = syntaxImplied(sub1Formula, var2, var3)
            val var9: Boolean = if (var6 != null) {
                var3.contains(var6) ?: false
            } else {
                true
            }
            if (formula !is Until && formula !is Or) {
                if (formula is Release) {
                    var8 && var7 || var8 && var9
                } else if (formula !is And) {
                    if (formula is Next) {
                        if (sub1Formula != null) {
                            var3.contains(sub1Formula) ?: false
                        } else {
                            true
                        }
                    } else {
                        false
                    }
                } else {
                    var8 && var7
                }
            } else {
                var7 || var8 && var9
            }
        }
    }

    private fun newId(): Int {
        return ++id
    }

    private fun unique(var1: Formula): Formula {
        val var2 = var1.toString()
        return if (subf.containsKey(var2)) {
            subf[var2] as Formula
        } else {
            var1.id = newId()
            subf[var2] = var1
            if (var1 is Proposition) {
                props.add(var1)
            }
            var1
        }
    }

    companion object {

        var normalLTL = true

    }

}