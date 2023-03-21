package io.github.vinccool96.ltsa.ltsatool.lts.ltl

import io.github.vinccool96.ltsa.ltsatool.lts.*
import java.util.*

class PredicateDefinition {

    var name: Symbol? = null

    var trueSet: ActionLabels? = null

    var falseSet: ActionLabels? = null

    var trueActions: Vector<String>? = null

    var falseActions: Vector<String>? = null

    var expr: Stack<Symbol>? = null

    var initial = false

    var range: ActionLabels? = null

    private constructor(var1: Symbol, var2: ActionLabels, var3: ActionLabels, var4: ActionLabels, var5: Stack<Symbol>) {
        name = var1
        range = var2
        trueSet = var3
        falseSet = var4
        expr = var5
        initial = false
    }

    constructor(var1: Symbol, var2: Vector<String>, var3: Vector<String>) {
        name = var1
        trueActions = var2
        falseActions = var3
    }

    constructor(var1: String, var2: Vector<String>, var3: Vector<String>, var4: Boolean) {
        name = Symbol(123, var1)
        trueActions = var2
        falseActions = var3
        initial = var4
    }

    fun query(var1: String): Int {
        return if (trueActions!!.contains(var1)) {
            1
        } else {
            if (falseActions!!.contains(var1)) -1 else 0
        }
    }

    fun initial(): Int {
        return if (initial) 1 else -1
    }

    override fun toString(): String {
        return name.toString()
    }

    companion object {

        val definitionsDelegate = lazy {
            Hashtable<String, PredicateDefinition>()
        }

        val definitions: Hashtable<String, PredicateDefinition> by definitionsDelegate

        fun put(var0: Symbol, var1: ActionLabels, var2: ActionLabels, var3: ActionLabels, var4: Stack<Symbol>) {
            if (definitions.put(var0.toString(), PredicateDefinition(var0, var1, var2, var3, var4)) != null) {
                Diagnostics.fatal("duplicate LTL predicate definition: $var0", var0 as Symbol?)
            }
        }

        operator fun contains(var0: Symbol): Boolean {
            return if (definitionsDelegate.isInitialized()) false else definitions.containsKey(var0.toString())
        }

        fun compileAll() {
            if (definitionsDelegate.isInitialized()) {
                val var0 = ArrayList<PredicateDefinition>()
                var0.addAll(definitions.values)
                val var1: Iterator<*> = var0.iterator()
                while (var1.hasNext()) {
                    val var2 = var1.next() as PredicateDefinition
                    compile(var2)
                }
            }
        }

        operator fun get(var0: String?): PredicateDefinition? {
            return if (!definitionsDelegate.isInitialized()) {
                null
            } else {
                val var1 = definitions[var0]
                if (var1 == null) {
                    null
                } else {
                    if (var1.range != null) null else var1
                }
            }
        }

        fun compile(var0: PredicateDefinition?) {
            if (var0 != null) {
                if (var0.range == null) {
                    var0.trueActions = var0.trueSet!!.getActions(null, null)
                    var0.falseActions = var0.falseSet!!.getActions(null, null)
                    assertDisjoint(var0.trueActions!!, var0.falseActions!!, var0)
                    if (var0.expr != null) {
                        val var1: Int = Expression.evaluate(var0.expr!!, null, null)
                        var0.initial = var1 > 0
                    }
                } else {
                    val var7 = Hashtable<String, Value>()
                    var0.range!!.initContext(var7, null)
                    while (var0.range!!.hasMoreNames()) {
                        val var2 = var0.range!!.nextName()
                        val var3 = var0.trueSet!!.getActions(var7, null)
                        val var4 = var0.falseSet!!.getActions(var7, null)
                        var var5 = false
                        assertDisjoint(var3, var4, var0)
                        if (var0.expr != null) {
                            val var6: Int = Expression.evaluate(var0.expr!!, var7, null)
                            var5 = var6 > 0
                        }
                        val var8 = var0.name.toString() + "." + var2
                        definitions[var8] = PredicateDefinition(var8, var3, var4, var5)
                    }
                    var0.range!!.clearContext()
                }
            }
        }

        private fun assertDisjoint(var0: Vector<String>, var1: Vector<String>, var2: PredicateDefinition) {
            val var3 = TreeSet(var0)
            var3.retainAll(var1)
            if (!var3.isEmpty()) {
                Diagnostics.fatal("Predicate " + var2.name + " True & False sets must be disjoint", var2.name)
            }
        }

    }

}