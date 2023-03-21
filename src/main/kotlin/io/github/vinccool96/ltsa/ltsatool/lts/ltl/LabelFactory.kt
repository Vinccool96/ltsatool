package io.github.vinccool96.ltsa.ltsatool.lts.ltl

import io.github.vinccool96.ltsa.ltsatool.lts.CompactState
import io.github.vinccool96.ltsa.ltsatool.lts.Diagnostics
import io.github.vinccool96.ltsa.ltsatool.lts.EventState
import io.github.vinccool96.ltsa.ltsatool.lts.Symbol
import io.github.vinccool96.ltsa.ltsatool.utils.toArrayOfNotNull
import java.util.*

class LabelFactory(var1: String?, var2: FormulaFactory, var3: Vector<String>) {

    var allprops: SortedSet<Proposition> = var2.props

    var fac: FormulaFactory = var2

    var alphaX: Vector<String> = var3

    var name: String? = var1

    var tr = HashMap<String, BitSet>()

    var ps: Array<BitSet>

    var nps: Array<BitSet>

    var propProcs: Vector<CompactState>? = null

    var allActions: SortedSet<String>? = null

    private val realFluents: MutableList<PredicateDefinition> = ArrayList()

    init {
        tr = HashMap()

        val var4 = allprops.size
        val ps = arrayOfNulls<BitSet>(var4)
        val nps = arrayOfNulls<BitSet>(var4)
        val var5 = BitSet(1 shl var4)

        var var6 = 0
        while (var6 < var4) {
            ps[var6] = BitSet(1 shl var4)
            nps[var6] = BitSet(1 shl var4)
            ++var6
        }

        this.ps = ps.toArrayOfNotNull()
        this.nps = nps.toArrayOfNotNull()

        var6 = 0
        while (var6 < 1 shl var4) {
            var5.set(var6)
            for (var7 in 0 until var4) {
                if ((var6 shr var7) % 2 == 1) {
                    this.ps[var7].set(var6)
                } else {
                    this.nps[var7].set(var6)
                }
            }
            ++var6
        }

        tr["true"] = var5

        this.compileProps()
    }

    val prefix: Vector<String>
        get() {
            val var1 = Vector<String>()
            val var2 = allprops.first() as Formula
            var1.add("_$var2")
            return var1
        }

    fun makeLabel(var1: SortedSet<out Formula>): String {
        val var2 = StringBuffer()
        val var3 = allprops.iterator()
        var var4 = false
        val var5 = BitSet()
        var var6 = 0
        while (var3.hasNext()) {
            val var7 = var3.next()
            if (var1.contains(var7)) {
                if (var4) {
                    var2.append("&")
                    var5.and(ps[var6])
                } else {
                    var5.or(ps[var6])
                    var4 = true
                }
                var2.append(var7)
            } else if (var1.contains(fac.makeNot(var7))) {
                if (var4) {
                    var2.append("&")
                    var5.and(nps[var6])
                } else {
                    var5.or(nps[var6])
                    var4 = true
                }
                var2.append("!$var7")
            }
            ++var6
        }
        val var8 = var2.toString()
        tr[var8] = var5
        return var8
    }

    fun makeAlphabet(): Array<String> {
        return this.makeAlphabet(null, null, null)
    }

    private fun makeAlphabet(var1: PredicateDefinition?, var2: BitSet?, var3: BitSet?): Array<String> {
        val var13 = if (var1 == null) {
            1
        } else {
            var1.trueActions!!.size + var1.falseActions!!.size
        }
        val var5 = (1 shl allprops.size) + 1 + var13
        val var6 = arrayOfNulls<String>(var5)
        var var7: Int
        var7 = 0
        while (var7 < var5 - var13) {
            val var8 = StringBuffer()
            val var9: Iterator<*> = allprops.iterator()
            var var10 = false
            var var11 = 0
            while (var9.hasNext()) {
                val var12 = var9.next() as Formula
                if (var10) {
                    var8.append(".")
                }
                var10 = true
                var8.append("_" + var12 + "." + (var7 shr var11) % 2)
                ++var11
            }
            var6[var7 + 1] = var8.toString()
            ++var7
        }
        var6[0] = "tau"
        if (var1 == null) {
            var6[var5 - 1] = "@$name"
        } else {
            var7 = var5 - var13
            var var14: Iterator<*>
            var14 = var1.falseActions!!.iterator()
            while (var14.hasNext()) {
                var6[var7] = var14.next()
                var3!!.set(var7)
                ++var7
            }
            var14 = var1.trueActions!!.iterator()
            while (var14.hasNext()) {
                var6[var7] = var14.next()
                var2!!.set(var7)
                ++var7
            }
        }
        return var6.toArrayOfNotNull()
    }

    val fluents: Array<PredicateDefinition>?
        get() {
            return if (this.realFluents.isEmpty()) {
                null
            } else {
                val var1 = arrayOfNulls<PredicateDefinition>(this.realFluents.size)
                for (var2 in var1.indices) {
                    var1[var2] = this.realFluents[var2]
                }
                var1.toArrayOfNotNull()
            }
        }

    protected fun compileProps() {
        propProcs = Vector()
        allActions = TreeSet()
        var var1 = allprops.iterator()
        var var3: Proposition
        var var4: PredicateDefinition?
        var var5: Vector<String>
        var var2 = 0
        while (var1.hasNext()) {
            var3 = var1.next() as Proposition
            var4 = PredicateDefinition[var3.toString()]
            if (var4 != null) {
                realFluents.add(var4)
                allActions!!.addAll(var4.trueActions!!)
                allActions!!.addAll(var4.falseActions!!)
                propProcs!!.add(makePropProcess(var4, var2))
            } else if (fac.actionPredicates != null && fac.actionPredicates!!.containsKey(var3.toString())) {
                var5 = fac.actionPredicates!![var3.toString()]!!
                allActions!!.addAll(var5)
            } else {
                Diagnostics.fatal("Proposition $var3 not found", var3.sym as Symbol?)
            }
            ++var2
        }
        if (alphaX != null) {
            allActions!!.addAll(alphaX)
        }
        var1 = allprops.iterator()
        var2 = 0
        while (var1.hasNext()) {
            var3 = var1.next() as Proposition
            var4 = PredicateDefinition[var3.toString()]
            if (var4 == null) {
                if (fac.actionPredicates != null && fac.actionPredicates!!.containsKey(var3.toString())) {
                    var5 = fac.actionPredicates!![var3.toString()]!!
                    val var6 = Vector<String>()
                    var6.addAll(allActions!!)
                    var6.removeAll(var5)
                    var4 = PredicateDefinition(Symbol(123, var3.toString()), var5, var6)
                    val var7 = makePropProcess(var4, var2)
                    propProcs!!.add(var7)
                } else {
                    Diagnostics.fatal("Proposition $var3 not found", var3.sym as Symbol?)
                }
            }
            ++var2
        }
        propProcs!!.add(makeSyncProcess())
    }

    fun makePropProcess(var1: PredicateDefinition, var2: Int): CompactState {
        val var3 = CompactState()
        var3.name = var1.name.toString()
        var3.maxStates = 2
        var3.states = arrayOfNulls(var3.maxStates)
        val var4 = BitSet()
        val var5 = BitSet()
        var3.alphabet = this.makeAlphabet(var1, var4, var5)
        val var6 = if (var1.initial) 1 else 0
        val var7 = if (var1.initial) 0 else 1
        var var8: Int = 0
        while (var8 < var4.size()) {
            if (var4[var8]) {
                var3.states[var6] = EventState.add(var3.states[var6], EventState(var8, var7))
            }
            ++var8
        }
        var8 = 0
        while (var8 < var5.size()) {
            if (var5[var8]) {
                var3.states[var7] = EventState.add(var3.states[var7], EventState(var8, var6))
            }
            ++var8
        }
        var8 = 0
        while (var8 < var5.size()) {
            if (var5[var8]) {
                var3.states[var6] = EventState.add(var3.states[var6], EventState(var8, var6))
            }
            ++var8
        }
        var8 = 0
        while (var8 < var4.size()) {
            if (var4[var8]) {
                var3.states[var7] = EventState.add(var3.states[var7], EventState(var8, var7))
            }
            ++var8
        }
        var8 = 0
        while (var8 < nps[var2].size()) {
            if (nps[var2][var8]) {
                var3.states[var6] = EventState.add(var3.states[var6], EventState(var8 + 1, var6))
            }
            ++var8
        }
        var8 = 0
        while (var8 < ps[var2].size()) {
            if (ps[var2][var8]) {
                var3.states[var7] = EventState.add(var3.states[var7], EventState(var8 + 1, var7))
            }
            ++var8
        }
        return var3
    }

    fun makeSyncProcess(): CompactState {
        val var1 = CompactState()
        var1.name = "SYNC"
        var1.maxStates = 2
        var1.states = arrayOfNulls(var1.maxStates)
        val var2 = this.makeAlphabet()
        val var3 = arrayOfNulls<String>(allActions!!.size)
        var var4 = 0
        val var5 = allActions!!.iterator()
        while (var5.hasNext()) {
            var3[var4++] = var5.next()
        }
        val alphabet = arrayOfNulls<String>(var2.size - 1 + var3.size)
        alphabet[0] = "tau"
        var var6 = 1
        while (var6 < var2.size - 1) {
            alphabet[var6] = var2[var6]
            var1.states[1] = EventState.add(var1.states[1], EventState(var6, 0))
            ++var6
        }
        var1.alphabet = alphabet.toArrayOfNotNull()
        var6 = 0
        while (var6 < var3.size) {
            var1.alphabet[var6 + var2.size - 1] = var3[var6]!!
            var1.states[0] = EventState.add(var1.states[0], EventState(var6 + var2.size - 1, 1))
            ++var6
        }
        return var1
    }

}