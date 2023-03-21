package io.github.vinccool96.ltsa.ltsatool.lts

import io.github.vinccool96.ltsa.ltsatool.lts.ltl.FluentTrace
import io.github.vinccool96.ltsa.ltsatool.utils.toArrayOfNotNull
import java.util.*


class CompositeState(var name: String, var machines: Vector<CompactState>) {

    var composition: CompactState? = null

    var hidden: Vector<Short>? = null

    var exposeNotHide = false

    var priorityIsLow = true

    var makeDeterministic = false

    var makeMinimal = false

    var makeCompose = false

    var isProperty = false

    var priorityLabels: Vector<Short>? = null

    var alphaStop = CompactState()

    protected var errorTrace = Vector<Short>()
        set(value) {
            field = Vector<Short>()
            field.addAll(value)
        }

    private val saved: CompactState? = null

    private val tracer: FluentTrace? = null

    constructor(machines: Vector<CompactState>) : this("DEFAULT", machines)

    init {
        alphaStop = CompactState()
        alphaStop.name = name
        alphaStop.maxStates = 1
        alphaStop.states = arrayOfNulls(alphaStop.maxStates)
        alphaStop.states[0] = null
        val var1 = Hashtable<String, String>()
        val var2 = machines.elements()

        while (var2.hasMoreElements()) {
            val var3 = var2.nextElement() as CompactState
            for (var4 in 1 until var3.alphabet.size) {
                var1[var3.alphabet[var4]] = var3.alphabet[var4]
            }
        }

        val alphaStopAlphabet = arrayOfNulls<String>(var1.size + 1)
        alphaStopAlphabet[0] = "tau"
        var var5 = 1

        val var6 = var1.keys()
        while (var6.hasMoreElements()) {
            val var7 = var6.nextElement()
            alphaStopAlphabet[var5] = var7
            ++var5
        }
        alphaStop.alphabet = alphaStopAlphabet.toArrayOfNotNull()
    }

    companion object {

        var reduceFlag = true

    }

}