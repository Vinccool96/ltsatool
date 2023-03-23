package io.github.vinccool96.ltsa.ltsatool.lts

import java.util.*


class CounterExample(protected var mach: CompositeState) {

    var errorTrace: Vector<String>? = null
        protected set

    fun print(var1: LTSOutput) {
        val var2 = EventState(0, 0)
        val var3 = EventState.search(var2, mach.composition!!.states, 0, -1, mach.composition!!.endseq)
        errorTrace = null
        when (var3) {
            -1 -> {
                errorTrace = EventState.getPath(var2.path, this.mach.composition!!.alphabet)
                val var4 = findComponent(errorTrace!!)
                var1.outln("Trace to property violation in $var4:")
                printPath(var1, errorTrace!!)
            }
            0 -> {
                var1.outln("Trace to DEADLOCK:")
                errorTrace = EventState.getPath(var2.path, this.mach.composition!!.alphabet)
                printPath(var1, errorTrace!!)
            }
            1 -> var1.outln("No deadlocks/errors")
        }
    }

    private fun printPath(var1: LTSOutput, var2: Vector<String>) {
        val var3 = var2.elements()
        while (var3.hasMoreElements()) {
            var1.outln("\t" + var3.nextElement())
        }
    }

    private fun findComponent(var1: Vector<String>): String? {
        val var2 = mach.machines.elements()
        var var3: CompactState
        do {
            if (!var2.hasMoreElements()) {
                return "?"
            }
            var3 = var2.nextElement() as CompactState
        } while (!var3.isErrorTrace(var1))
        return var3.name
    }

}