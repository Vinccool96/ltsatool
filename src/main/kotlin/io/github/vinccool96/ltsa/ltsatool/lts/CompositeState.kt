package io.github.vinccool96.ltsa.ltsatool.lts

import io.github.vinccool96.ltsa.ltsatool.lts.ltl.FluentTrace
import io.github.vinccool96.ltsa.ltsatool.utils.toArrayOfNotNull
import java.util.*

class CompositeState(var name: String, var machines: Vector<CompactState>) {

    var composition: CompactState? = null

    var hidden: Vector<String>? = null

    var exposeNotHide = false

    var priorityIsLow = true

    var makeDeterministic = false

    var makeMinimal = false

    var makeCompose = false

    var isProperty = false

    var priorityLabels: Vector<String>? = null

    val alphaStop = CompactState()

    var errorTrace: MutableList<String> = Vector()
        set(value) {
            field = Vector()
            (field as Vector<String>).addAll(value)
        }

    private var saved: CompactState? = null

    var fluentTracer: FluentTrace? = null

    constructor(machines: Vector<CompactState>) : this("DEFAULT", machines)

    init {
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

    fun compose(var1: LTSOutput) {
        this.compose(var1, false)
    }

    fun compose(var1: LTSOutput, var2: Boolean) {
        if (machines != null && machines.size > 0) {
            val var3 = Analyser(this, var1, null, var2)
            composition = var3.composeNoHide()
            if (makeDeterministic) {
                applyHiding()
                determinise(var1)
            } else if (makeMinimal) {
                applyHiding()
                minimise(var1)
            } else {
                applyHiding()
            }
        }
    }

    private fun applyHiding() {
        if (composition != null) {
            if (hidden != null) {
                if (!exposeNotHide) {
                    composition!!.conceal(hidden!!)
                } else {
                    composition!!.expose(hidden!!)
                }
            }
        }
    }

    fun analyse(var1: LTSOutput) {
        if (saved != null) {
            machines.remove(saved)
            saved = null
        }
        if (composition != null) {
            val var2 = CounterExample(this)
            var2.print(var1)
            errorTrace = var2.errorTrace!!
        } else {
            val var3 = Analyser(this, var1, null)
            var3.analyse()
            if (var3.errorTrace != null) {
                this.errorTrace = (var3.errorTrace as MutableList<String>)
            }
        }
    }

    fun checkProgress(var1: LTSOutput) {
        if (saved != null) {
            machines.remove(saved)
            saved = null
        }
        val var2: ProgressCheck
        if (composition != null) {
            var2 = ProgressCheck(composition!!, var1)
            var2.doProgressCheck()
        } else {
            val var3 = Analyser(this, var1, null as EventManager?)
            var2 = ProgressCheck(var3, var1)
            var2.doProgressCheck()
        }
        errorTrace = var2.errorTrace!!
    }

    fun checkLTL(var1: LTSOutput, var2: CompositeState) {
        val var3 = var2.composition
        if (name == "DEFAULT" && machines.size == 0) {
            machines = var2.machines
            composition = var2.composition
        } else {
            if (saved != null) {
                machines.remove(saved)
            }
            val var4 = hidden
            val var5 = exposeNotHide
            hidden = var3!!.alphabetV
            exposeNotHide = true
            machines.add(var3.also { saved = it })
            val var6 = Analyser(this, var1, null)
            if (!var2.composition!!.hasERROR()) {
                val var7 = ProgressCheck(var6, var1, var2.fluentTracer)
                var7.doLTLCheck()
                errorTrace = var7.errorTrace!!
            } else {
                var6.analyse(var2.fluentTracer!!)
                if (var6.errorTrace != null) {
                    this.errorTrace = var6.errorTrace as MutableList<String>
                }
            }
            hidden = var4
            exposeNotHide = var5
        }
    }

    fun minimise(var1: LTSOutput) {
        if (composition != null) {
            if (reduceFlag) {
                composition!!.removeNonDetTau()
            }
            val var2 = Minimiser(composition!!, var1)
            composition = var2.minimise()
        }
    }

    fun determinise(var1: LTSOutput) {
        if (composition != null) {
            val var2 = Minimiser(composition!!, var1)
            composition = var2.traceMinimise()
            if (isProperty) {
                composition!!.makeProperty()
            }
        }
    }

    fun create(var1: LTSOutput): CompactState? {
        this.compose(var1)
        return composition
    }

    fun needNotCreate(): Boolean {
        return hidden == null && priorityLabels == null && !makeDeterministic && !makeMinimal && !makeCompose
    }

    fun prefixLabels(var1: String) {
        name = "$var1:$name"
        alphaStop.prefixLabels(var1)
        val var2: Enumeration<*> = machines.elements()
        while (var2.hasMoreElements()) {
            val var3 = var2.nextElement() as CompactState
            var3.prefixLabels(var1)
        }
    }

    fun addAccess(var1: Vector<String>) {
        val var2: Int = var1.size
        if (var2 != 0) {
            var var3 = "{"
            val var4: Enumeration<*> = var1.elements()
            var var5 = 0
            while (var4.hasMoreElements()) {
                val var6 = var4.nextElement() as String
                var3 += var6
                ++var5
                if (var5 < var2) {
                    var3 = "$var3,"
                }
            }
            name = "$var3}::$name"
            alphaStop.addAccess(var1)
            val var8 = machines.elements()
            while (var8.hasMoreElements()) {
                val var7 = var8.nextElement()
                var7.addAccess(var1)
            }
        }
    }

    fun relabel(var1: Relation, var2: LTSOutput): CompactState? {
        alphaStop.relabel(var1)
        return if (alphaStop.relabelDuplicates() && machines.size > 1) {
            this.compose(var2)
            composition!!.relabel(var1)
            composition
        } else {
            val var3 = machines.elements()
            while (var3.hasMoreElements()) {
                val var4 = var3.nextElement()
                var4.relabel(var1)
            }
            null
        }
    }

    companion object {

        var reduceFlag = true

    }

}