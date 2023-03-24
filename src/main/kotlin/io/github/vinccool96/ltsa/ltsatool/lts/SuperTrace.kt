package io.github.vinccool96.ltsa.ltsatool.lts

import java.util.*

class SuperTrace(var mach: Automata, var output: LTSOutput) {

    var nbits = HASHSIZE * 1024 * 8

    var table = BitSet(nbits)

    var stack = MyStack()

    var errorTrace: LinkedList<String>? = null

    var nstate = 0

    var nTrans = 0

    init {
        analyse()
    }

    fun analyse() {
        output.outln("Analysing using Supertrace (Depth bound $DEPTHBOUND" + " Hashtable size " + HASHSIZE + "K )...")
        System.gc()
        val var1 = System.currentTimeMillis()
        val var3 = search()
        val var4 = System.currentTimeMillis()
        outStatistics(stack.depth, nstate, nTrans)
        if (var3 == 1) {
            output.outln("Trace to DEADLOCK:")
            errorTrace = computeTrace(false)
            if (errorTrace!!.size <= 100) {
                printPath(errorTrace!!)
            } else {
                output.outln("Trace length " + errorTrace!!.size + ", replay using Check/Run")
            }
        } else if (var3 == 2) {
            output.outln("Trace to property violation in " + mach.violatedProperty + ":")
            errorTrace = computeTrace(true)
            if (errorTrace!!.size <= 100) {
                printPath(errorTrace!!)
            } else {
                output.outln("Trace length " + errorTrace!!.size + ", replay using Check/Run")
            }
        } else {
            output.outln("No deadlocks/errors")
        }
        output.outln("Analysed using Supertrace in: " + (var4 - var1) + "ms")
    }

    private fun hashOne(var1: ByteArray?): Int {
        return StateCodec.hash(var1!!)
    }

    private fun hashTwo(var1: ByteArray?): Int {
        var var2 = StateCodec.hashLong(var1!!)
        var2 += 1325656567898L
        val var4 = (var2 xor (var2 ushr 32)).toInt()
        return var4 and Int.MAX_VALUE
    }

    private fun put(var1: ByteArray?) {
        table.set(hashOne(var1) % nbits)
        table.set(hashTwo(var1) % nbits)
    }

    private operator fun contains(var1: ByteArray?): Boolean {
        return table[hashOne(var1) % nbits] && table[hashTwo(var1) % nbits]
    }

    private fun search(): Int {
        val var1 = mach.START()
        var var2: MyHash? = null
        if (mach.isPartialOrder) {
            var2 = MyHash(DEPTHBOUND + 1)
            mach.setStackChecker(var2)
        }
        stack.push(var1)
        put(var1)
        while (true) {
            while (!stack.empty()) {
                if (stack.marked()) {
                    var2?.remove(stack.peek()!!)
                    stack.pop()
                } else {
                    ++nstate
                    if (nstate % 10000 == 0) {
                        outStatistics(stack.depth, nstate, nTrans)
                    }
                    val var3 = stack.peek()
                    stack.mark()
                    var2?.put(var3!!)
                    val var4 = mach.getTransitions(var3)
                    if (var4.empty() && !mach.END(var3!!)) {
                        return 1
                    }
                    while (!var4.empty()) {
                        ++nTrans
                        if (var4.to == null) {
                            return 2
                        }
                        if (stack.depth < DEPTHBOUND && !this.contains(var4.to)) {
                            stack.push(var4.to)
                            put(var4.to)
                        }
                        var4.next()
                    }
                }
            }
            return 0
        }
    }

    private fun outStatistics(var1: Int, var2: Int, var3: Int) {
        output.out("-- Depth: $var1 States: $var2 Transitions: $var3")
        val var4 = Runtime.getRuntime()
        output.outln(" Memory used: " + (var4.totalMemory() - var4.freeMemory()) / 1000L + "K")
    }

    private fun printPath(var1: LinkedList<String>) {
        val var2 = var1.iterator()
        while (var2.hasNext()) {
            output.outln("\t" + var2.next())
        }
    }

    private fun computeTrace(var1: Boolean): LinkedList<String> {
        mach.disablePartialOrder()
        val var2 = LinkedList<String>()
        if (var1) {
            while (true) {
                if (stack.marked()) {
                    var2.addFirst(findAction(stack.peek(), null))
                    break
                }
                stack.pop()
            }
        }
        var var3 = stack.pop()
        while (!stack.empty()) {
            if (!stack.marked()) {
                stack.pop()
            } else {
                var2.addFirst(findAction(stack.peek(), var3))
                var3 = stack.pop()
            }
        }
        return var2
    }

    private fun findAction(var1: ByteArray?, var2: ByteArray?): String {
        val var3 = mach.getTransitions(var1)
        while (!var3.empty()) {
            if (StateCodec.equals(var3.to, var2)) {
                return mach.alphabet[var3.action]
            }
            var3.next()
        }
        return "ACTION NOT FOUND"
    }

    companion object {

        var DEPTHBOUND = 100000

        var HASHSIZE = 8000

        private const val SUCCESS = 0

        private const val DEADLOCK = 1

        private const val ERROR = 2

    }

}