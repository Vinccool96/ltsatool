package io.github.vinccool96.ltsa.ltsatool.lts

import io.github.vinccool96.ltsa.ltsatool.utils.toArrayOfNotNull
import java.util.*

class PartialOrder(var1: Hashtable<String, BitSet>, private val names: Array<String>,
        private val machines: Array<CompactState>, private val checker: StackChecker, var5: Vector<String>?,
        var6: Boolean, private val preserveOE: Boolean, private val high: BitSet?) {

    private val actionSharedBy: Array<IntArray>

    private val candidates: Array<IntArray>

    private val partners: Array<IntArray>

    private val Nactions = names.size

    private val visible: BitSet

    init {
        val actionSharedBy = arrayOfNulls<IntArray>(Nactions)
        var var9 = 1
        while (var9 < names.size) {
            val var10 = var1[names[var9]]!!
            actionSharedBy[var9] = this.bitsToArray(var10)
            ++var9
        }
        this.actionSharedBy = actionSharedBy.toArrayOfNotNull()
        visible = BitSet(Nactions)
        var9 = 1
        while (var9 < names.size) {
            if (var5 == null) {
                visible.set(var9)
            } else if (var6) {
                if (CompactState.contains(names[var9], var5)) {
                    visible.set(var9)
                }
            } else if (!CompactState.contains(names[var9], var5)) {
                visible.set(var9)
            }
            ++var9
        }

        val partners = arrayOfNulls<IntArray>(machines.size)

        for (i in machines.indices) {
            partners[i] = IntArray(machines[i].states.size)
            for (var2 in 0 until machines[i].states.size) {
                partners[i]!![var2] = -1
            }
        }
        this.partners = partners.toArrayOfNotNull()
        this.candidates = this.computeCandidates()
    }

    fun transitions(var1: IntArray): List<IntArray>? {
        var var2 = 0
        while (var2 < machines.size) {
            if (candidates[var2][var1[var2]] == 1) {
                val var3 = ArrayList<IntArray>(8)
                val var4 = getMachTransitions(var3, var2, var1, null as BitSet?)
                if (var4) {
                    return var3
                }
            }
            ++var2
        }
        var2 = 0
        while (var2 < machines.size) {
            if (candidates[var2][var1[var2]] == 2) {
                val var5 = partners[var2][var1[var2]]
                if (var2 == partners[var5][var1[var5]]) {
                    val var6 = getPairTransitions(var2, var5, var1)
                    if (var6 != null) {
                        return var6
                    }
                }
            }
            ++var2
        }
        return null
    }

    private fun addTransitions(var1: MutableList<IntArray>, var2: IntArray, var3: Int, var4: Int): Boolean {
        var var5: IntArray? = null
        val var6 = actionSharedBy[var3][var4]
        var var7 = machines[var6].states[var2[var6]]
        if (var7 != null) {
            var5 = myclone(var2, var3)
        }
        var7 = EventState.firstCompState(var7, var3, var2)
        if (var4 < actionSharedBy[var3].size - 1) {
            if (!addTransitions(var1, var2, var3, var4 + 1)) {
                return false
            }
        } else {
            if (checker.onStack(var2)) {
                return false
            }
            var1.add(var2)
        }
        while (var7 != null) {
            val var8 = myclone(var5, var3)
            var7 = EventState.moreCompState(var7, var8)
            if (var4 < actionSharedBy[var3].size - 1) {
                if (!addTransitions(var1, var8, var3, var4 + 1)) {
                    return false
                }
            } else {
                if (checker.onStack(var8)) {
                    return false
                }
                var1.add(var8)
            }
        }
        return true
    }

    private fun getPairTransitions(var1: Int, var2: Int, var3: IntArray): List<IntArray>? {
        val var4 = ArrayList<IntArray>(8)
        var var5 = true
        var var6: BitSet?
        if (!preserveOE) {
            var6 = getUnshared(var1, var3)
            if (var6 != null) {
                var5 = getMachTransitions(var4, var1, var3, var6)
            }
            if (!var5) {
                return null
            }
            var6 = getUnshared(var2, var3)
            if (var6 != null) {
                var5 = getMachTransitions(var4, var2, var3, var6)
            }
            if (!var5) {
                return null
            }
        }
        var6 = BitSet(Nactions)
        EventState.hasEvents(machines[var1].states[var3[var1]], var6)
        val var7 = BitSet(Nactions)
        EventState.hasEvents(machines[var2].states[var3[var2]], var7)
        var6.and(var7)
        return if (preserveOE && countSet(var6) != 1) {
            null
        } else {
            var6.clear(0)
            val var8 = bitsToArray(var6)
            for (var9 in var8!!.indices) {
                var5 = addTransitions(var4, myclone(var3, var8[var9]), var8[var9], 0)
                if (!var5) {
                    return null
                }
            }
            var4
        }
    }

    private fun getUnshared(var1: Int, var2: IntArray): BitSet? {
        val var3 = BitSet(Nactions)
        val var4 = machines[var1].states[var2[var1]]!!.elements()
        while (var4.hasMoreElements()) {
            val var5 = var4.nextElement()
            if (var5.event == 0) {
                var3.set(var5.event)
            } else if (actionSharedBy[var5.event].size == 1) {
                var3.set(var5.event)
            }
        }
        return if (var3.size() == 0) {
            null
        } else {
            var3
        }
    }

    private fun getMachTransitions(var1: MutableList<IntArray>, var2: Int, var3: IntArray, var4: BitSet?): Boolean {
        val var5 = machines[var2].states[var3[var2]]!!.elements()
        while (true) {
            var var6: EventState
            do {
                if (!var5.hasMoreElements()) {
                    return true
                }
                var6 = var5.nextElement()
            } while (var4 != null && !var4[var6.event])
            val var7 = myclone(var3, var6.event)
            var7[var2] = var6.next
            if (checker.onStack(var7)) {
                return false
            }
            var1.add(var7)
        }
    }

    private fun bitsToArray(var1: BitSet): IntArray? {
        val var2 = countSet(var1)
        return if (var2 == 0) {
            null
        } else {
            val var3 = IntArray(var2)
            var var4 = 0
            val var5 = var1.size()
            for (var6 in 0 until var5) {
                if (var1[var6]) {
                    var3[var4] = var6
                    ++var4
                }
            }
            var3
        }
    }

    private fun countSet(var1: BitSet): Int {
        var var2 = 0
        val var3 = var1.size()
        for (var4 in 0 until var3) {
            if (var1[var4]) {
                ++var2
            }
        }
        return var2
    }

    private fun myclone(var1: IntArray?, var2: Int): IntArray {
        val var3 = IntArray(var1!!.size)
        for (var4 in 0 until var1.size - 1) {
            var3[var4] = var1[var4]
        }
        var3[var1.size - 1] = var2
        return var3
    }

    private fun printArray(var1: String, var2: Array<IntArray>) {
        println(var1)
        for (var3 in var2.indices) {
            print("Mach $var3 --")
            for (var4 in var2[var3].indices) {
                print(" " + var2[var3][var4])
            }
            println(".")
        }
    }

    private fun computeCandidates(): Array<IntArray> {
        val var1 = arrayOfNulls<IntArray>(machines.size)
        for (var2 in machines.indices) {
            var1[var2] = IntArray(machines[var2].states.size)
            for (var3 in 0 until machines[var2].states.size) {
                val var4 = EventState.localEnabled(machines[var2].states[var3])
                var1[var2]!![var3] = candidateNumber(var2, var3, var4)
            }
        }
        return var1.toArrayOfNotNull()
    }

    private fun candidateNumber(var1: Int, var2: Int, var3: IntArray?): Int {
        return if (var3 == null) {
            0
        } else if (preserveOE && EventState.hasNonDet(machines[var1].states[var2])) {
            0
        } else {
            var var4 = 0
            var var5 = 0
            var var6 = -1
            for (var7 in var3.indices) {
                val var9 = var3[var7]
                if (visible[var9]) {
                    return 0
                }
                if (high != null && !high[var9]) {
                    return 0
                }
                val var10 = if (var9 == 0) {
                    1
                } else {
                    actionSharedBy[var9].size
                }
                if (var10 == 1) {
                    ++var5
                }
                if (var10 > var4) {
                    var4 = var10
                }
                if (var4 > 2) {
                    return 0
                }
                if (var10 == 2) {
                    if (var6 < 0) {
                        var6 = getPartner(var1, var9)
                    } else if (var6 != getPartner(var1, var9)) {
                        return 0
                    }
                }
            }
            if (!preserveOE || var5 <= 1 && (var4 != 2 || var5 <= 0)) {
                if (var4 == 2) {
                    partners[var1][var2] = var6
                }
                var4
            } else {
                0
            }
        }
    }

    private fun getPartner(var1: Int, var2: Int): Int {
        return if (actionSharedBy[var2][0] == var1) actionSharedBy[var2][1] else actionSharedBy[var2][0]
    }

}