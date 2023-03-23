package io.github.vinccool96.ltsa.ltsatool.lts

import io.github.vinccool96.ltsa.ltsatool.lts.ltl.FluentTrace
import java.util.*
import kotlin.math.min


class ProgressCheck(private var mach: Automata, var output: LTSOutput, var tracer: FluentTrace?) {

    private var stack: Stack<ByteArray>? = null

    var id = 0

    var ncomp = 0

    var violation = 0

    var hasERROR = false

    var tnames: String? = null

    var accept = -1

    var progress = false

    private var sccId = 0

    private var nTrans = 0

    private var realErrorTrace: Vector<String>? = null

    var cycleTrace: Vector<String>? = null

    constructor(mach: Automata, output: LTSOutput) : this(mach, output, null)

    fun doProgressCheck() {
        progress = true
        output.outln("Progress Check...")
        val var1 = System.currentTimeMillis()
        ProgressTest.initTests(mach.alphabet)
        stack = Stack()
        findCC()
        val var3 = System.currentTimeMillis()
        if (hasERROR) {
            output.outln("Safety property violation detected - check safety.")
        } else if (violation == 0) {
            output.outln("No progress violations detected.")
        } else if (violation > MAX_VIOLATION) {
            output.outln("More than 10 violations")
        }
        output.outln("Progress Check in: " + (var3 - var1) + "ms")
    }

    fun doLTLCheck() {
        progress = false
        output.outln("LTL Property Check...")
        val var1 = System.currentTimeMillis()
        accept = acceptLabel(mach.alphabet)
        if (accept < 0) {
            output.outln("No labeled acceptance states.")
        } else {
            stack = Stack()
            findCC()
            val var3 = System.currentTimeMillis()
            if (hasERROR) {
                output.outln("Safety property violation detected - check safety.")
            } else if (violation == 0) {
                output.outln("No LTL Property violations detected.")
            } else if (violation > MAX_VIOLATION) {
                output.outln("More than 10 violations")
            }
            output.outln("LTL Property Check in: " + (var3 - var1) + "ms")
        }
    }

    fun numberComponents(): Int {
        return ncomp
    }

    private fun findCC() {
        val var1 = MyHashProg()
        val var2 = MyStack()
        mach.setStackChecker(var1)
        sccId = 0
        nTrans = 0
        val var4 = mach.START()
        var2.push(var4)
        var1.add(var4, null)
        while (!var2.empty()) {
            var var3: MyHashProgEntry?
            var3 = var1[var2.peek()!!]
            while (var3!!.isReturn || var3.isProcessed) {
                if (var3.isReturn && !var3.isProcessed) {
                    var3.isProcessed = true
                    if (var3.parent != null) {
                        var3.parent!!.low = min(var3.parent!!.low, var3.low)
                    }
                    if (var3.low == var3.dfn && component(var1, stack!!, var3.key)) {
                        return
                    }
                }
                var2.pop()
                if (var2.empty()) {
                    outStatistics(sccId, nTrans)
                    return
                }
                var3 = var1[var2.peek()!!]
            }
            var3.dfn = ++sccId
            var3.low = var3.dfn
            if (sccId % 10000 == 0) {
                outStatistics(sccId, nTrans)
            }
            stack!!.push(var3.key)
            var3.isReturn = true
            val var5 = mach.getTransitions(var3.key)
            while (!var5.empty()) {
                ++nTrans
                if (var5.to == null) {
                    hasERROR = true
                    return
                }
                if (accept < 0 || var5.action != accept) {
                    val var6 = var1[var5.to!!]
                    if (var6 == null) {
                        var1.add(var5.to!!, var3)
                        var2.push(var5.to)
                    } else if (var6.dfn == 0) {
                        var6.parent = var3
                        var2.push(var5.to)
                    } else if (var6.dfn < var3.dfn) {
                        var3.low = min(var6.dfn, var3.low)
                    }
                }
                var5.next()
            }
        }
        outStatistics(sccId, nTrans)
    }

    private fun outhse(var1: MyHashProgEntry) {
        output.outln("state: ${var1.key} dfn: ${var1.dfn} low: ${var1.low} ret ${var1.isReturn}")
    }

    private fun component(var1: MyHashProg, var2: Stack<ByteArray>, var3: ByteArray): Boolean {
        ++ncomp
        var var4 = false
        val var5 = Stack<ByteArray>()
        val var6 = BitSet(mach.alphabet.size)
        var var8 = var3
        var var7: ByteArray
        do {
            var5.push(var2.pop())
            var7 = var5.peek()
            if (progress) {
                val var9 = mach.getTransitions(var7)
                while (!var9.empty()) {
                    val var10 = var9.action
                    var6.set(var10)
                    var9.next()
                }
            } else if (!var4) {
                var4 = mach.isAccepting(var7)
                if (var4) {
                    var8 = var7
                }
            }
        } while (!StateCodec.equals(var7, var3))
        if (progress) {
            if (missing(var6) && terminalComponent(var1, var5)) {
                outStatistics(sccId, nTrans)
                printCycle(var5, var6, var3)
                return true
            }
        } else if (var4) {
            if (!strongFairFlag) {
                if (nontrivial(var5)) {
                    outStatistics(sccId, nTrans)
                    printCounterExample(var5, var8)
                    return true
                }
            } else if (terminalComponent(var1, var5)) {
                outStatistics(sccId, nTrans)
                printCounterExample(null, var8)
                return true
            }
        }
        var var11: MyHashProgEntry?
        val var12: Enumeration<*> = var5.elements()
        while (var12.hasMoreElements()) {
            var11 = var1[(var12.nextElement() as ByteArray)]
            var11!!.dfn = Int.MAX_VALUE
        }
        return false
    }

    private fun missing(var1: BitSet): Boolean {
        val var2 = mach.alphabet.size
        if (ProgressTest.noTests()) {
            for (var3 in 1 until var2) {
                if (!var1[var3]) {
                    return true
                }
            }
        } else {
            tnames = null
            val var5 = ProgressTest.tests!!.elements()
            while (var5.hasMoreElements()) {
                val var4 = var5.nextElement()
                if (var4.cset == null) {
                    if (containsNoneOf(var2, var1, var4.pset!!)) {
                        if (tnames == null) {
                            tnames = var4.name
                        } else {
                            tnames = tnames + " " + var4.name
                        }
                    }
                } else if (!containsNoneOf(var2, var1, var4.pset!!) && containsNoneOf(var2, var1, var4.cset!!)) {
                    tnames = if (tnames == null) {
                        var4.name
                    } else {
                        tnames + " " + var4.name
                    }
                }
            }
            if (tnames != null) {
                return true
            }
        }
        return false
    }

    private fun containsNoneOf(var1: Int, var2: BitSet, var3: BitSet): Boolean {
        for (var4 in 1 until var1) {
            if (var2[var4] && var3[var4]) {
                return false
            }
        }
        return true
    }

    private fun terminalComponent(var1: MyHashProg, var2: Vector<ByteArray>): Boolean {
        val var3 = BitSet(10001)
        var var4 = var2.elements()
        var var5: ByteArray
        while (var4.hasMoreElements()) {
            var5 = var4.nextElement()
            val var6 = var1[var5]
            var3.set(var6!!.dfn)
        }
        var4 = var2.elements()
        while (var4.hasMoreElements()) {
            var5 = var4.nextElement() as ByteArray
            val var8 = mach.getTransitions(var5)
            while (!var8.empty()) {
                if (var8.to == null) {
                    hasERROR = true
                    return false
                }
                val var7 = var1[var8.to!!] ?: return false
                if (var7.dfn == 0) {
                    return false
                }
                if (var7.dfn == Int.MAX_VALUE) {
                    return false
                }
                if (!var3[var7.dfn]) {
                    return false
                }
                var8.next()
            }
        }
        return true
    }

    private fun inComponent(var1: Vector<ByteArray>, var2: ByteArray): Boolean {
        val var3 = var1.elements()
        var var4: ByteArray
        do {
            if (!var3.hasMoreElements()) {
                return false
            }
            var4 = var3.nextElement()
        } while (!StateCodec.equals(var4, var2))
        return true
    }

    private fun nontrivial(var1: Vector<ByteArray>): Boolean {
        return if (var1.size > 1) {
            true
        } else {
            val var2 = var1.elementAt(0)
            val var3 = mach.getTransitions(var2)
            while (!var3.empty()) {
                val var4 = var3.action
                if ((var4 != accept || accept < 0) && StateCodec.equals(var2, var3.to)) {
                    return true
                }
                var3.next()
            }
            false
        }
    }

    private fun printSet(var1: BitSet, var2: Boolean) {
        val var3 = Vector<String>()
        val var4 = mach.alphabet
        for (var5 in 1 until var4.size) {
            if (var2 && !var1[var5] || !var2 && var1[var5]) {
                var3.addElement(var4[var5])
            }
        }
        output.outln("\t" + Alphabet(var3).toString())
    }

    val errorTrace: Vector<String>?
        get() {
            return if (realErrorTrace == null) {
                null
            } else {
                if (cycleTrace != null) {
                    realErrorTrace!!.addAll(cycleTrace!!)
                    realErrorTrace!!.addAll(cycleTrace!!)
                }
                realErrorTrace
            }
        }

    private fun printCycle(var1: Stack<ByteArray>, var2: BitSet, var3: ByteArray) {
        ++violation
        if (violation <= MAX_VIOLATION) {
            realErrorTrace = getRootTrace(var3)
            if (realErrorTrace != null) {
                cycleTrace = getCycleTrace(null, var3)
                if (ProgressTest.noTests()) {
                    output.outln("Progress violation for actions: ")
                    printSet(var2, true)
                } else {
                    output.outln("Progress violation: $tnames")
                }
                output.outln("Trace to terminal set of states:")
                printTrace(realErrorTrace!!)
                output.outln("Cycle in terminal set:")
                printTrace(cycleTrace!!)
                output.outln("Actions in terminal set:")
                printSet(var2, false)
            }
        }
    }

    private fun printCounterExample(var1: Stack<ByteArray>?, var2: ByteArray) {
        ++violation
        if (violation <= MAX_VIOLATION) {
            realErrorTrace = getRootTrace(var2)
            if (realErrorTrace != null) {
                cycleTrace = getCycleTrace(var1, var2)
                output.outln("Violation of LTL property: " + mach.alphabet[accept])
                output.outln("Trace to terminal set of states:")
                tracer!!.print(output, realErrorTrace, true)
                output.outln("Cycle in terminal set:")
                tracer!!.print(output, cycleTrace, false)
            }
        }
    }

    fun getRootTrace(var1: ByteArray): Vector<String>? {
        output.outln("Finding trace to cycle...")
        val var2: Vector<String>? = mach.getTraceToState(mach.START(), var1)
        if (var2 == null) {
            hasERROR = true
        }
        return var2
    }

    fun getCycleTrace(var1: Vector<ByteArray>?, var2: ByteArray): Vector<String>? {
        output.outln("Finding trace in cycle...")
        var var3: Vector<String>? = null
        val var4 = mach.getTransitions(var2)
        var var5: ByteArray? = null
        var var6 = 0
        while (!var4.empty()) {
            var6 = var4.action
            if ((var6 != accept || accept <= 0) && !stateLabel(var6)) {
                var5 = var4.to
                if (var1 == null || inComponent(var1, var5!!)) {
                    break
                }
                var4.next()
            } else {
                var4.next()
            }
        }
        if (var5 != null) {
            var3 = mach.getTraceToState(var5, var2)!!
            var3.add(0, mach.alphabet[var6])
        }
        return var3
    }

    private fun printTrace(var1: Vector<String>?) {
        if (var1 != null) {
            val var2 = var1.elements()
            while (var2.hasMoreElements()) {
                output.outln("\t" + var2.nextElement() as String)
            }
        }
    }

    private fun stateLabel(var1: Int): Boolean {
        val var2 = mach.alphabet[var1]
        return var2[0] == '_'
    }

    private fun outStatistics(var1: Int, var2: Int) {
        val var3 = Runtime.getRuntime()
        output.outln(
                "-- States: " + var1 + " Transitions: " + var2 + " Memory used: " + (var3.totalMemory() - var3.freeMemory()) / 1000L + "K")
    }

    private fun isAccept(var1: String): Boolean {
        return if (var1[0] == '@') {
            true
        } else {
            var var2 = 0
            var var3 = var1.indexOf(46.toChar())
            while (var3 > 0) {
                if (var1.substring(var2, var3)[0] == '@') {
                    return true
                }
                var2 = var3 + 1
                var3 = var1.indexOf(46.toChar(), var3 + 1)
            }
            var1.substring(var2)[0] == '@'
        }
    }

    private fun acceptLabel(var1: Array<String>): Int {
        for (var2 in 1 until var1.size) {
            if (isAccept(var1[var2])) {
                return var2
            }
        }
        return -1
    }

    companion object {

        var strongFairFlag = true

        const val MAX_VIOLATION = 10

    }

}