package io.github.vinccool96.ltsa.ltsatool.lts

import java.io.PrintStream
import java.util.*


class EventState(var event: Int, var next: Int) {

    var machine = 0

    var list: EventState? = null

    var nondet: EventState? = null

    var path: EventState? = null

    fun elements(): Enumeration<EventState> {
        return EventStateEnumerator(this)
    }

    companion object {

        fun add(var0: EventState?, var1: EventState): EventState {
            return if (var0 != null && var1.event >= var0.event) {
                var var2: EventState?
                var2 = var0
                while (var2!!.list != null && var2.event != var1.event && var1.event >= var2.list!!.event) {
                    var2 = var2.list
                }
                if (var2.event == var1.event) {
                    var var3 = var2
                    if (var2.next == var1.next) {
                        return var0
                    }
                    while (var3!!.nondet != null) {
                        var3 = var3.nondet
                        if (var3!!.next == var1.next) {
                            return var0
                        }
                    }
                    var3.nondet = var1
                } else {
                    var1.list = var2.list
                    var2.list = var1
                }
                var0
            } else {
                var1.list = var0
                var1
            }
        }

        fun remove(var0: EventState?, var1: EventState): EventState? {
            return if (var0 == null) {
                var0
            } else if (var0.event == var1.event && var0.next == var1.next) {
                if (var0.nondet == null) {
                    var0.list
                } else {
                    var0.nondet!!.list = var0.list
                    var0.nondet
                }
            } else {
                var var2 = var0
                var var3: EventState = var0
                while (var2 != null) {
                    var var4 = var2
                    var var5: EventState = var2
                    while (var4 != null) {
                        if (var4.event == var1.event && var4.next == var1.next) {
                            if (var2 === var4) {
                                if (var2.nondet == null) {
                                    var3.list = var2.list
                                    return var0
                                }
                                var2.nondet!!.list = var2.list
                                var3.list = var2.nondet
                                return var0
                            }
                            var5.nondet = var4.nondet
                            return var0
                        }
                        var5 = var4
                        var4 = var4.nondet
                    }
                    var3 = var2
                    var2 = var2.list
                }
                var0
            }
        }

        fun printAUT(var0: EventState?, var1: Int, var2: Array<String>, var3: PrintStream) {
            var var4 = var0
            while (var4 != null) {
                var var5 = var4
                while (var5 != null) {
                    var3.print("($var1,${var2[var5.event]},${var5.next})")
                    var5 = var5.nondet
                }
                var4 = var4.list
            }
        }

        fun count(var0: EventState?): Int {
            var var1 = var0
            var var2: Int = 0
            while (var1 != null) {
                var var3 = var1
                while (var3 != null) {
                    ++var2
                    var3 = var3.nondet
                }
                var1 = var1.list
            }
            return var2
        }

        fun hasState(var0: EventState?, var1: Int): Boolean {
            var var2 = var0
            while (var2 != null) {
                var var3 = var2
                while (var3 != null) {
                    if (var3.next == var1) {
                        return true
                    }
                    var3 = var3.nondet
                }
                var2 = var2.list
            }
            return false
        }

        fun replaceWithError(var0: EventState?, var1: Int) {
            var var2 = var0
            while (var2 != null) {
                var var3 = var2
                while (var3 != null) {
                    if (var3.next == var1) {
                        var3.next = -1
                    }
                    var3 = var3.nondet
                }
                var2 = var2.list
            }
        }

        fun offsetSeq(var0: Int, var1: Int, var2: Int, var3: EventState?): EventState? {
            var var4 = var3
            while (var4 != null) {
                var var5 = var4
                while (var5 != null) {
                    if (var5.next >= 0) {
                        if (var5.next == var1) {
                            var5.next = var2
                        } else {
                            var5.next += var0
                        }
                    }
                    var5 = var5.nondet
                }
                var4 = var4.list
            }
            return var3
        }

        fun toState(var0: EventState?, var1: Int): Int {
            var var2 = var0
            while (var2 != null) {
                var var3 = var2
                while (var3 != null) {
                    if (var3.next == var1) {
                        return var3.event
                    }
                    var3 = var3.nondet
                }
                var2 = var2.list
            }
            return -1
        }

        fun countStates(var0: EventState?, var1: Int): Int {
            var var2 = var0
            var var3: Int
            var3 = 0
            while (var2 != null) {
                var var4 = var2
                while (var4 != null) {
                    if (var4.next == var1) {
                        ++var3
                    }
                    var4 = var4.nondet
                }
                var2 = var2.list
            }
            return var3
        }

        fun hasEvent(var0: EventState?, var1: Int): Boolean {
            var var2 = var0
            while (var2 != null) {
                if (var2.event == var1) {
                    return true
                }
                var2 = var2.list
            }
            return false
        }

        fun isAccepting(var0: EventState?, var1: Array<String>): Boolean {
            var var2 = var0
            while (var2 != null) {
                if (var1[var2.event][0] == '@') {
                    return true
                }
                var2 = var2.list
            }
            return false
        }

        fun isTerminal(var0: Int, var1: EventState?): Boolean {
            var var2 = var1
            while (var2 != null) {
                var var3 = var2
                while (var3 != null) {
                    if (var3.next != var0) {
                        return false
                    }
                    var3 = var3.nondet
                }
                var2 = var2.list
            }
            return true
        }

        fun firstCompState(var0: EventState?, var1: Int, var2: IntArray): EventState? {
            var var3 = var0
            while (var3 != null) {
                if (var3.event == var1) {
                    var2[var3.machine] = var3.next
                    return var3.nondet
                }
                var3 = var3.list
            }
            return null
        }

        fun moreCompState(var0: EventState, var1: IntArray): EventState? {
            var1[var0.machine] = var0.next
            return var0.nondet
        }

        fun hasTau(var0: EventState?): Boolean {
            return if (var0 == null) {
                false
            } else {
                var0.event == 0
            }
        }

        fun hasOnlyTau(var0: EventState?): Boolean {
            return if (var0 == null) {
                false
            } else {
                var0.event == 0 && var0.list == null
            }
        }

        fun hasOnlyTauAndAccept(var0: EventState?, var1: Array<String>): Boolean {
            return if (var0 == null) {
                false
            } else if (var0.event != 0) {
                false
            } else if (var0.list == null) {
                true
            } else if (var1[var0.list!!.event][0] != '@') {
                false
            } else {
                var0.list!!.list == null
            }
        }

        fun removeAccept(var0: EventState): EventState {
            var0.list = null
            return var0
        }

        fun addNonDetTau(var0: EventState?, var1: Array<EventState?>, var2: BitSet): EventState? {
            var var3 = var0
            var var4: EventState?
            var4 = null
            while (var3 != null) {
                var var5 = var3
                while (var5 != null) {
                    if (var5.next > 0 && var2[var5.next]) {
                        val var6 = nextState(var1[var5.next], 0)
                        var5.next = var6!![0]
                        for (var7 in 1 until var6.size) {
                            var4 = add(var4, EventState(var5.event, var6[var7]))
                        }
                    }
                    var5 = var5.nondet
                }
                var3 = var3.list
            }
            return var4?.let { union(var0, it) } ?: var0
        }

        fun hasNonDet(var0: EventState?): Boolean {
            var var1 = var0
            while (var1 != null) {
                if (var1.nondet != null) {
                    return true
                }
                var1 = var1.list
            }
            return false
        }

        fun hasNonDetEvent(var0: EventState?, var1: Int): Boolean {
            var var2 = var0
            while (var2 != null) {
                if (var2.event == var1 && var2.nondet != null) {
                    return true
                }
                var2 = var2.list
            }
            return false
        }

        fun localEnabled(var0: EventState?): IntArray? {
            var var1 = var0
            var var2: Int
            var2 = 0
            while (var1 != null) {
                ++var2
                var1 = var1.list
            }
            return if (var2 == 0) {
                null
            } else {
                val var3 = IntArray(var2)
                var1 = var0
                var2 = 0
                while (var1 != null) {
                    var3[var2++] = var1.event
                    var1 = var1.list
                }
                var3
            }
        }

        fun hasEvents(var0: EventState?, var1: BitSet) {
            var var2 = var0
            while (var2 != null) {
                var1.set(var2.event)
                var2 = var2.list
            }
        }

        fun nextState(var0: EventState?, var1: Int): IntArray? {
            var var2 = var0
            while (var2 != null) {
                if (var2.event == var1) {
                    var var3 = var2
                    var var4: Int
                    var4 = 0
                    while (var3 != null) {
                        var3 = var3.nondet
                        ++var4
                    }
                    var3 = var2
                    val var5 = IntArray(var4)
                    for (var6 in var5.indices) {
                        var5[var6] = var3!!.next
                        var3 = var3.nondet
                    }
                    return var5
                }
                var2 = var2.list
            }
            return null
        }

        fun renumberEvents(var0: EventState?, var1: Hashtable<*, *>): EventState? {
            var var2 = var0
            var var3: EventState?
            var3 = null
            while (var2 != null) {
                var var4 = var2
                while (var4 != null) {
                    val var5 = var1[var4.event] as Int
                    var3 = add(var3, EventState(var5, var4.next))
                    var4 = var4.nondet
                }
                var2 = var2.list
            }
            return var3
        }

        fun newTransitions(var0: EventState?, var1: Relation): EventState? {
            var var2 = var0
            var var3: EventState? = null
            while (var2 != null) {
                var var4 = var2
                while (var4 != null) {
                    val var5 = var1[var4.event]
                    if (var5 != null) {
                        if (var5 is Int) {
                            var3 = add(var3, EventState(var5, var4.next))
                        } else {
                            val var6 = var5 as Vector<Int>
                            val var7 = var6.elements()
                            while (var7.hasMoreElements()) {
                                var3 = add(var3, EventState(var7.nextElement(), var4.next))
                            }
                        }
                    }
                    var4 = var4.nondet
                }
                var2 = var2.list
            }
            return var3
        }

        fun offsetEvents(var0: EventState?, var1: Int): EventState? {
            var var2 = var0
            val var3: Any?
            var3 = null
            while (var2 != null) {
                var var4 = var2
                while (var4 != null) {
                    var4.event = if (var4.event == 0) 0 else var4.event + var1
                    var4 = var4.nondet
                }
                var2 = var2.list
            }
            return var3
        }

        fun renumberStates(var0: EventState?, var1: Hashtable<*, *>): EventState? {
            var var2 = var0
            var var3: EventState?
            var3 = null
            while (var2 != null) {
                var var4 = var2
                while (var4 != null) {
                    val var5 = if (var4.next < 0) -1 else (var1[var4.next] as Int?)!!
                    var3 = add(var3, EventState(var4.event, var5))
                    var4 = var4.nondet
                }
                var2 = var2.list
            }
            return var3
        }

        fun renumberStates(var0: EventState?, var1: MyIntHash): EventState? {
            var var2 = var0
            var var3: EventState?
            var3 = null
            while (var2 != null) {
                var var4 = var2
                while (var4 != null) {
                    val var5 = if (var4.next < 0) -1 else var1[var4.next]
                    var3 = add(var3, EventState(var4.event, var5))
                    var4 = var4.nondet
                }
                var2 = var2.list
            }
            return var3
        }

        fun addTransToError(var0: EventState?, var1: Int): EventState? {
            var var2 = var0
            var var3: EventState? = null
            if (var0 != null && var0.event == 0) {
                var2 = var0.list
            }
            var var4: Int
            var var5: Int
            var4 = 1
            while (var2 != null) {
                if (var4 < var2.event) {
                    var5 = var4
                    while (var5 < var2.event) {
                        var3 = add(var3, EventState(var5, -1))
                        ++var5
                    }
                }
                var4 = var2.event + 1
                var var6 = var2
                while (var6 != null) {
                    var3 = add(var3, EventState(var6.event, var6.next))
                    var6 = var6.nondet
                }
                var2 = var2.list
            }
            var5 = var4
            while (var5 < var1) {
                var3 = add(var3, EventState(var5, -1))
                ++var5
            }
            return var3
        }

        fun removeTransToError(var0: EventState?): EventState? {
            var var1 = var0
            var var2: EventState?
            var2 = null
            while (var1 != null) {
                if (var1.next != -1) {
                    var2 = add(var2, EventState(var1.event, var1.next))
                }
                var1 = var1.list
            }
            return var2
        }

        fun removeTau(var0: EventState?): EventState? {
            return if (var0 == null) {
                var0
            } else {
                if (var0.event != 0) var0 else var0.list
            }
        }

        fun tauAdd(var0: EventState?, var1: Array<EventState?>): EventState? {
            var var0 = var0
            var var2 = var0
            var var3: EventState? = null
            if (var0 != null && var0.event == 0) {
                var2 = var0.list
            }
            while (var2 != null) {
                var var4 = var2
                while (var4 != null) {
                    if (var4.next != -1) {
                        var var5 = var1[var4.next]
                        while (var5 != null) {
                            var3 = push(var3, EventState(var2.event, var5.next))
                            var5 = var5.nondet
                        }
                    }
                    var4 = var4.nondet
                }
                var2 = var2.list
            }
            while (var3 != null) {
                var0 = add(var0, var3)
                var3 = pop(var3)
            }
            return var0
        }

        fun setActions(var0: EventState?, var1: BitSet) {
            var var2 = var0
            while (var2 != null) {
                var1.set(var2.event)
                var2 = var2.list
            }
        }

        fun actionAdd(var0: EventState?, var1: Array<EventState?>): EventState? {
            var var0 = var0
            return if (var0 != null && var0.event == 0) {
                var var2 = var0
                while (var2 != null) {
                    if (var2.next != -1) {
                        var0 = union(var0, var1[var2.next])
                    }
                    var2 = var2.nondet
                }
                var0
            } else {
                var0
            }
        }

        fun union(var0: EventState?, var1: EventState?): EventState? {
            var var2 = var0
            var var3 = var1
            while (var3 != null) {
                var var4 = var3
                while (var4 != null) {
                    var2 = add(var2, EventState(var4.event, var4.next))
                    var4 = var4.nondet
                }
                var3 = var3.list
            }
            return var2
        }

        fun transpose(var0: EventState?): EventState? {
            var var1: EventState? = null
            var var2: EventState?
            var var3: EventState?
            var2 = var0
            while (var2 != null) {
                var3 = var2
                while (var3 != null) {
                    var1 = add(var1, EventState(var3.next, var3.event))
                    var3 = var3.nondet
                }
                var2 = var2.list
            }
            var2 = var1
            while (var2 != null) {
                var3 = var2
                while (var3 != null) {
                    val var4 = var3.next
                    var3.next = var3.event
                    var3.event = var4
                    var3 = var3.nondet
                }
                var2 = var2.list
            }
            return var1
        }

        fun eventsToNext(var0: EventState?, var1: Array<String?>): Array<String?> {
            var var2 = var0
            var var3: Int
            var3 = 0
            while (var2 != null) {
                var2 = var2.nondet
                ++var3
            }
            var2 = var0
            val var4 = arrayOfNulls<String>(var3)
            for (var5 in var4.indices) {
                var4[var5] = var1[var2!!.event]
                var2 = var2.nondet
            }
            return var4
        }

        fun eventsToNextNoAccept(var0: EventState?, var1: Array<String>): Array<String?> {
            var var2 = var0
            var var3: Int
            var3 = 0
            while (var2 != null) {
                if (var1[var2.event][0] != '@') {
                    ++var3
                }
                var2 = var2.nondet
            }
            var2 = var0
            val var4 = arrayOfNulls<String>(var3)
            var var5 = 0
            while (var5 < var4.size) {
                if (var1[var2!!.event][0] != '@') {
                    var4[var5] = var1[var2.event]
                } else {
                    --var5
                }
                var2 = var2.nondet
                ++var5
            }
            return var4
        }

        private fun push(var0: EventState?, var1: EventState?): EventState {
            if (var0 == null) {
                var1!!.path = var1
            } else {
                var1!!.path = var0
            }
            return var1
        }

        private fun inStack(var0: EventState): Boolean {
            return var0.path != null
        }

        private fun pop(var0: EventState?): EventState? {
            var var0: EventState? = var0
            return if (var0 == null) {
                null
            } else {
                val var1: EventState = var0
                var0 = var0.path
                var1.path = null
                if (var0 === var1) null else var0
            }
        }

        fun reachableTau(var0: Array<EventState?>, var1: Int): EventState? {
            var var2 = var0[var1]
            if (var2 != null && var2.event == 0) {
                val var3 = BitSet(var0.size)
                var3.set(var1)
                var var4: EventState?
                var4 = null
                while (var2 != null) {
                    var4 = push(var4, var2)
                    var2 = var2.nondet
                }
                while (true) {
                    var var6: EventState?
                    do {
                        do {
                            var var5: Int
                            do {
                                if (var4 == null) {
                                    return var2
                                }
                                var5 = var4.next
                                var2 = add(var2, EventState(0, var5))
                                var4 = pop(var4)
                            } while (var5 == -1)
                            var3.set(var5)
                            var6 = var0[var5]
                        } while (var6 == null)
                    } while (var6!!.event != 0)
                    while (var6 != null) {
                        if (!inStack(var6) && (var6.next < 0 || !var3[var6.next])) {
                            var4 = push(var4, var6)
                        }
                        var6 = var6.nondet
                    }
                }
            } else {
                return null
            }
        }

        private fun addtail(var0: EventState?, var1: EventState): EventState {
            var1.path = null
            if (var0 != null) {
                var0.path = var1
            }
            return var1
        }

        private fun removehead(var0: EventState?): EventState? {
            return if (var0 == null) {
                null
            } else {
                var0.path!!
            }
        }

        fun reachable(var0: Array<EventState?>): MyIntHash {
            var var1 = 0
            val var2 = MyIntHash(var0.size)
            var var3: EventState? = null
            var3 = push(var3, EventState(0, 0))
            while (true) {
                var var4: Int
                do {
                    if (var3 == null) {
                        return var2
                    }
                    var4 = var3.next
                    var3 = pop(var3)
                } while (var2.containsKey(var4))
                var2[var4] = var1++
                var var5 = var0[var4]
                while (var5 != null) {
                    var var6 = var5
                    while (var6 != null) {
                        if (var6.next >= 0 && !var2.containsKey(var6.next)) {
                            var3 = push(var3, var6)
                        }
                        var6 = var6.nondet
                    }
                    var5 = var5.list
                }
            }
        }

        fun search(var0: EventState, var1: Array<EventState?>, var2: Int, var3: Int, var4: Int): Int {
            val var5 = EventState(0, var2)
            var var6: EventState? = var5
            var var7: EventState? = var5
            var var8: Byte = 1
            val var10: Array<EventState?> = arrayOfNulls(var1.size + 1)
            var var12: EventState?
            while (var6 != null) {
                val var11 = var6.next
                var10[var11 + 1] = var6
                if (var11 < 0 || var11 == var3) {
                    var8 = -1
                    break
                }
                var12 = var1[var11]
                if (var12 == null && var11 != var4) {
                    var8 = 0
                    break
                }
                while (var12 != null) {
                    var var13 = var12
                    while (var13 != null) {
                        if (var10[var13.next + 1] == null) {
                            var13.machine = var11
                            var7 = addtail(var7, var13)
                            var10[var13.next + 1] = var5
                        }
                        var13 = var13.nondet
                    }
                    var12 = var12.list
                }
                var6 = removehead(var6)
            }
            return if (var6 == null) {
                var8.toInt()
            } else {
                var var14: EventState? = null
                var12 = var6
                while (var12!!.next != var2) {
                    var14 = push(var14, var12)
                    var12 = var10[var12.machine + 1]
                }
                var0.path = var14
                var8.toInt()
            }
        }

        fun printPath(var0: EventState?, var1: Array<String>, var2: LTSOutput) {
            var var3 = var0
            while (var3 != null) {
                var2.outln("\t" + var1[var3.event])
                var3 = pop(var3)
            }
        }

        fun getPath(var0: EventState?, var1: Array<String?>): Vector<String> {
            var var2 = var0
            val var3 = Vector<String>()
            while (var2 != null) {
                var3.addElement(var1[var2.event])
                var2 = pop(var2)
            }
            return var3
        }

    }

}