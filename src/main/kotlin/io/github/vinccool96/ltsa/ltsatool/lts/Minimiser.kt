package io.github.vinccool96.ltsa.ltsatool.lts

import io.github.vinccool96.ltsa.ltsatool.utils.toArrayOfNotNull
import java.util.*


class Minimiser(var machine: CompactState, var output: LTSOutput) {

    lateinit var E: Array<BitSet>

    lateinit var A: Array<BitSet>

    var T: Array<EventState?>? = null

    private fun initTau() {
        T = arrayOfNulls(machine.states.size)
        for (var1 in 0 until T!!.size) {
            T!![var1] = EventState.reachableTau(machine.states, var1)!!
        }
    }

    private fun machTau(var1: CompactState): CompactState {
        var var2 = 0
        while (var2 < var1.states.size) {
            var1.states[var2] = EventState.tauAdd(var1.states[var2], T!!)
            ++var2
        }
        var2 = 0
        while (var2 < var1.states.size) {
            var1.states[var2] = EventState.union(var1.states[var2], T!![var2])
            var1.states[var2] = EventState.actionAdd(var1.states[var2], var1.states)
            ++var2
        }
        var2 = 0
        while (var2 < var1.states.size) {
            var1.states[var2] = EventState.add(var1.states[var2], EventState(0, var2))
            ++var2
        }
        output.out(".")
        return var1
    }

    private fun removeTau(var1: CompactState): CompactState {
        for (var2 in var1.states.indices) {
            var1.states[var2] = EventState.removeTau(var1.states[var2])
        }
        return var1
    }

    private fun initialise() {
        val A = arrayOfNulls<BitSet>(machine.maxStates)
        var var1 = 0
        while (var1 < A.size) {
            A[var1] = BitSet(machine.alphabet.size)
            EventState.setActions(machine.states[var1], A[var1]!!)
            ++var1
        }
        this.A = A.toArrayOfNotNull()
        val E = arrayOfNulls<BitSet>(machine.maxStates)
        var1 = 0
        while (var1 < E.size) {
            E[var1] = BitSet(E.size)
            ++var1
        }
        var1 = 0
        while (var1 < E.size) {
            E[var1]!!.set(var1)
            for (var2 in 0 until var1) {
                if (this.A[var1] == this.A[var2]) {
                    E[var1]!!.set(var2)
                    E[var2]!!.set(var1)
                }
            }
            ++var1
        }
        this.E = E.toArrayOfNotNull()
        output.out(".")
    }

    private fun dominimise() {
        var var1 = true
        while (var1) {
            output.out(".")
            var1 = false
            for (var2 in E.indices) {
                Thread.yield()
                for (var3 in 0 until var2) {
                    if (E[var2][var3]) {
                        val var4 = isEquivalent(var2, var3) && isEquivalent(var3, var2)
                        if (!var4) {
                            var1 = true
                            E[var2].clear(var3)
                            E[var3].clear(var2)
                        }
                    }
                }
            }
        }
    }

    fun minimise(): CompactState {
        output.out(machine.name + " minimising")
        val var1 = System.currentTimeMillis()
        val var3 = machine.myclone()
        if (machine.endseq >= 0) {
            val var4 = machine.endseq
            machine.states[var4] = EventState.add(machine.states[var4], EventState(machine.alphabet.size, var4))
        }
        if (machine.hasTau()) {
            initTau()
            machine = machTau(machine)
            T = null
        }
        initialise()
        dominimise()
        machine = var3
        val var7 = makeNewMachine()
        val var5 = System.currentTimeMillis()
        output.outln("")
        output.outln("Minimised States: " + var7.maxStates + " in " + (var5 - var1) + "ms")
        return var7
    }

    fun traceMinimise(): CompactState {
        var var1 = false
        if (machine.hasTau()) {
            var1 = true
            output.out("Eliminating tau")
            initTau()
            machine = machTau(machine)
            machine = removeTau(machine)
            T = null
        }
        if (var1 || machine.isNonDeterministic()) {
            var1 = true
            val var2 = Determinizer(machine, output)
            machine = var2.determine()
        }
        return if (var1) minimise() else machine
    }

    private fun isEquivalent(var1: Int, var2: Int): Boolean {
        var var3 = machine.states[var1]
        while (var3 != null) {
            var var4 = var3
            while (var4 != null) {
                if (!findSuccessor(var2, var4)) {
                    return false
                }
                var4 = var4.nondet
            }
            var3 = var3.list
        }
        return true
    }

    private fun findSuccessor(var1: Int, var2: EventState): Boolean {
        var var3: EventState?
        var3 = machine.states[var1]
        while (var3!!.event != var2.event) {
            var3 = var3.list
        }
        while (var3 != null) {
            if (var2.next < 0) {
                if (var3.next < 0) {
                    return true
                }
            } else if (var3.next >= 0 && E[var2.next][var3.next]) {
                return true
            }
            var3 = var3.nondet
        }
        return false
    }

    private fun makeNewMachine(): CompactState {
        val var1 = Hashtable<Int, Int>()
        val var2 = Hashtable<Int, Int>()
        val var3 = Counter(0)
        for (var4 in E.indices) {
            var var6 = var1[var4]
            if (var6 == null) {
                var1[var4] = var3.label.also { var6 = it }
                var2[var6] = var4
            }
            for (var7 in E.indices) {
                if (E[var4][var7]) {
                    var1[var7] = var6
                }
            }
        }
        val var8 = CompactState()
        var8.name = machine.name
        var8.maxStates = var2.size
        var8.alphabet = machine.alphabet
        var8.states = arrayOfNulls(var8.maxStates)
        if (machine.endseq < 0) {
            var8.endseq = machine.endseq
        } else {
            var8.endseq = var1[machine.endseq]!!
            var8.states[var8.endseq] =
                    EventState.remove(var8.states[var8.endseq], EventState(var8.alphabet.size, var8.endseq))
        }
        var var9 = 0
        while (var9 < machine.maxStates) {
            val var10 = var1[var9] as Int
            val var11 = EventState.renumberStates(machine.states[var9], var1)
            var8.states[var10] = EventState.union(var8.states[var10], var11)
            ++var9
        }
        var9 = 0
        while (var9 < var8.maxStates) {
            var8.states[var9] = EventState.remove(var8.states[var9], EventState(0, var9))
            ++var9
        }
        return var8
    }

    fun print(var1: LTSOutput) {
        privPrint(var1, E)
    }

    private fun privPrint(var1: LTSOutput, var2: Array<BitSet>) {
        if (var2.size <= 20) {
            val var3 = CharArray(var2.size * 2)
            var var4 = 0
            while (var4 < var2.size * 2) {
                var3[var4] = ' '
                ++var4
            }
            var1.outln("E:")
            var1.out("       ")
            var4 = 0
            while (var4 < var2.size) {
                var1.out(" $var4")
                ++var4
            }
            var1.outln("")
            var4 = 0
            while (var4 < var2.size) {
                var1.out("State $var4 ")
                for (var5 in var2.indices) {
                    if (var2[var4][var5]) {
                        var3[var5 * 2] = '1'
                    } else {
                        var3[var5 * 2] = ' '
                    }
                }
                var1.outln(String(var3))
                ++var4
            }
        }
    }

    companion object {

        const val TAU = 0

    }

}