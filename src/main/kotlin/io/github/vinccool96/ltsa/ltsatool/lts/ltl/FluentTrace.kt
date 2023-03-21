package io.github.vinccool96.ltsa.ltsatool.lts.ltl

import io.github.vinccool96.ltsa.ltsatool.lts.LTSOutput

class FluentTrace(fluents: Array<PredicateDefinition>?) {

    val fluents = fluents ?: arrayOf()

    val state = IntArray(this.fluents.size)

    private val noFluents = fluents == null

    private fun initialise() {
        if (!noFluents) {
            for (var1 in state.indices) {
                state[var1] = fluents[var1].initial()
            }
        }
    }

    private fun update(var1: String) {
        if (!noFluents) {
            for (var2 in state.indices) {
                val var3 = fluents[var2].query(var1)
                if (var3 != 0) {
                    state[var2] = var3
                }
            }
        }
    }

    private val fluentString: String
        get() {
            return if (noFluents) {
                ""
            } else {
                val var1 = StringBuffer()
                var1.append("\t\t")
                var var2 = true
                for (var3 in state.indices) {
                    if (state[var3] > 0) {
                        if (!var2) {
                            var1.append(" && ")
                        }
                        var1.append(fluents[var3].toString())
                        var2 = false
                    }
                }
                var1.toString()
            }
        }

    fun print(var1: LTSOutput, var2: List<String>?, var3: Boolean) {
        if (var2 != null) {
            if (var3) {
                initialise()
            }
            val var4 = var2.iterator()
            while (var4.hasNext()) {
                val var5 = var4.next()
                update(var5)
                var1.outln("\t$var5$fluentString")
            }
        }
    }

}