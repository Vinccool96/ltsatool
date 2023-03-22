package io.github.vinccool96.ltsa.ltsatool.lts

import io.github.vinccool96.ltsa.ltsatool.utils.toArrayOfNotNull
import java.util.*


class Determinizer(val machine: CompactState, val output: LTSOutput) {

    var newStates: Vector<EventState>? = null

    var stateSets: Vector<BitSet>? = null

    var map: Hashtable<BitSet, Int>? = null

    var nextState = 0

    var currentState = 0

    fun determine(): CompactState {
        output.outln("make DFA(" + machine.name + ")")
        newStates = Vector(machine.maxStates * 2)
        stateSets = Vector(machine.maxStates * 2)
        map = Hashtable(machine.maxStates * 2)
        nextState = 0
        currentState = 0
        val var1 = BitSet()
        var1.set(0)
        addState(var1)
        while (currentState < nextState) {
            compute(currentState)
            ++currentState
        }
        return makeNewMachine()
    }

    protected fun compute(var1: Int) {
        val var2 = stateSets!!.elementAt(var1)
        var var3: EventState? = null
        var var4: EventState? = null
        for (var5 in 0 until var2.size()) {
            if (var2[var5]) {
                var3 = EventState.union(var3, machine.states[var5])
            }
        }
        var var10 = var3
        while (var10 != null) {
            var var6 = false
            val var7 = BitSet()
            if (var10.next != -1) {
                var7.set(var10.next)
            } else {
                var6 = true
            }
            var var8 = var10.nondet
            while (var8 != null) {
                if (var8.next != -1) {
                    var7.set(var8.next)
                } else {
                    var6 = true
                }
                var8 = var8.nondet
            }
            val var9 = if (var6) {
                -1
            } else {
                addState(var7)
            }
            var4 = EventState.add(var4, EventState(var10.event, var9))
            var10 = var10.list
        }
        newStates!!.addElement(var4!!)
    }

    protected fun addState(var1: BitSet): Int {
        val var2 = map!!.get(var1)
        return if (var2 != null) {
            var2
        } else {
            map!![var1] = nextState
            stateSets!!.addElement(var1)
            ++nextState
            nextState - 1
        }
    }

    protected fun makeNewMachine(): CompactState {
        val var1 = CompactState()
        var1.name = machine.name
        val var1Alphabet = arrayOfNulls<String>(machine.alphabet.size)
        var var2 = 0
        while (var2 < machine.alphabet.size) {
            var1Alphabet[var2] = machine.alphabet[var2]
            ++var2
        }
        var1.alphabet = var1Alphabet.toArrayOfNotNull()
        var1.maxStates = nextState
        var1.states = arrayOfNulls(var1.maxStates)
        var2 = 0
        while (var2 < var1.maxStates) {
            var1.states[var2] = newStates!!.elementAt(var2)
            ++var2
        }
        if (machine.endseq >= 0) {
            val var4 = BitSet()
            var4.set(machine.endseq)
            val var3 = map!![var4]
            if (var3 != null) {
                var1.endseq = var3
            }
        }
        output.outln("DFA(" + machine.name + ") has " + var1.maxStates + " states.")
        return var1
    }

}