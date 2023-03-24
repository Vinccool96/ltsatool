package io.github.vinccool96.ltsa.ltsatool.lts

import io.github.vinccool96.ltsa.ltsatool.lts.Diagnostics.fatal
import io.github.vinccool96.ltsa.ltsatool.utils.toArrayOfNotNull
import java.util.*


class StateMachine(var1: ProcessSpec, var2: Vector<Value>?) {

    val name: String = var1.getname()

    val kludgeName = if (var2 != null) {
        var1.doParams(var2)
        this.name + paramString(var2)
    } else {
        this.name
    }

    var alphabet = Hashtable<String, Int>()

    var hidden: Vector<String>? = null

    var relabels: Relation? = null

    var explicit_states = Hashtable<String, Int>()

    val constants: Hashtable<String, Value>? = var1.constants

    var eventLabel = Counter(0)

    var stateLabel = Counter(0)

    var transitions = Vector<Transition>()

    var isProperty = false

    var isMinimal = false

    var isDeterministic = false

    var exposeNotHide = false

    var sequentialInserts: Hashtable<Int, CompactState>? = null

    var preInsertsLast: Hashtable<Int, Int>? = null

    var preInsertsMach: Hashtable<Int, CompactState>? = null

    var aliases = Hashtable<Int, Int>()

    init {
        alphabet["tau"] = eventLabel.label
        var1.explicitStates(this)
        var1.crunch(this)
        val var3 = IntArray(stateLabel.lastLabel)
        var var4 = 0
        while (var4 < var3.size) {
            var3[var4] = var4++
        }
        var var5: Int
        var var6: Int
        val var10 = aliases.keys()
        while (var10.hasMoreElements()) {
            var5 = var10.nextElement() as Int
            var6 = aliases[var5]!!
            var3[var5] = var6
        }
        for (var11 in var3.indices) {
            this.crunch(var11, var3)
        }
        val var12 = Counter(0)
        val var13 = Hashtable<Int, Int>()
        var var8: Int
        for (var7 in var3.indices) {
            var8 = var3[var7]
            var var9: Int
            if (!var13.containsKey(var8)) {
                var9 = if (var3[var7] >= 0) this.number(var8, var12) else -1
                var13[var8] = var9
                var3[var7] = var9
            } else {
                var9 = var13[var8]!!
                var3[var7] = var9
            }
        }
        this.insertSequential(var3)
        val var15 = explicit_states.keys()
        while (var15.hasMoreElements()) {
            val var14 = var15.nextElement()
            val var16 = explicit_states[var14]
            if (var16!! >= 0) {
                explicit_states[var14] = var3[var16]
            }
        }
        stateLabel = var12
        var1.transition(this)
        var1.addAlphabet(this)
        var1.relabelAlphabet(this)
        var1.hideAlphabet(this)
        isProperty = var1.isProperty
        isMinimal = var1.isMinimal
        isDeterministic = var1.isDeterministic
        exposeNotHide = var1.exposeNotHide
    }

    constructor(var1: ProcessSpec) : this(var1, null)

    fun makeCompactState(): CompactState {
        var var1 = CompactState()
        var1.name = kludgeName
        var1.maxStates = stateLabel.lastLabel
        val var2 = explicit_states["END"]
        if (var2 != null) {
            var1.endseq = var2
        }
        val var1Alphabet = arrayOfNulls<String>(alphabet.size)
        var var4: String
        var var5: Int
        val var3 = alphabet.keys()
        while (var3.hasMoreElements()) {
            var4 = var3.nextElement()
            var5 = alphabet[var4]!!
            if (var4 == "@") {
                var4 = "@" + var1.name
            }
            var1Alphabet[var5] = var4
        }
        var1.alphabet = var1Alphabet.toArrayOfNotNull()
        var1.states = arrayOfNulls(var1.maxStates)
        var var6: Transition
        val var8 = transitions.elements()
        while (var8.hasMoreElements()) {
            var6 = var8.nextElement()
            var5 = alphabet["" + var6.event]!!
            var1.states[var6.from] = EventState.add(var1.states[var6.from], EventState(var5, var6.to))
        }
        if (sequentialInserts != null) {
            var1.expandSequential(sequentialInserts!!)
        }
        if (relabels != null) {
            var1.relabel(relabels!!)
        }
        if (hidden != null) {
            if (!exposeNotHide) {
                var1.conceal(hidden!!)
            } else {
                var1.expose(hidden!!)
            }
        }
        if (isProperty) {
            if (var1.isNonDeterministic() || var1.hasTau()) {
                fatal("primitive property processes must be deterministic: $name")
            }
            var1.makeProperty()
        }
        check_for_ERROR(var1)
        var1.reachable()
        var var7: Minimiser
        if (isMinimal) {
            var7 = Minimiser(var1, output!!)
            var1 = var7.minimise()
        }
        if (isDeterministic) {
            var7 = Minimiser(var1, output!!)
            var1 = var7.traceMinimise()
        }
        return var1
    }

    fun check_for_ERROR(var1: CompactState) {
        if (explicit_states[name] == -1) {
            var1.states = arrayOfNulls(1)
            var1.maxStates = 1
            var1.states[0] = EventState.add(var1.states[0], EventState(0, -1))
        }
    }

    fun addSequential(var1: Int?, var2: CompactState?) {
        if (sequentialInserts == null) {
            sequentialInserts = Hashtable()
        }
        sequentialInserts!![var1] = var2
    }

    fun preAddSequential(var1: Int, var2: Int, var3: CompactState) {
        if (preInsertsLast == null) {
            preInsertsLast = Hashtable()
        }
        if (preInsertsMach == null) {
            preInsertsMach = Hashtable()
        }
        preInsertsLast!![var1] = var2
        preInsertsMach!![var1] = var3
    }

    private fun insertSequential(var1: IntArray) {
        if (preInsertsMach != null) {
            val var2: Enumeration<*> = preInsertsMach!!.keys()
            while (var2.hasMoreElements()) {
                val var3 = var2.nextElement() as Int
                val var4 = preInsertsMach!![var3]
                val var5 = preInsertsLast!![var3]
                val var6 = var1[var3]
                var4!!.offsetSeq(var6, (if (var5!! >= 0) var1[var5] else var5))
                addSequential(var6, var4)
            }
        }
    }

    private fun number(var1: Int, var2: Counter): Int {
        return if (preInsertsMach == null) {
            var2.label
        } else {
            val var3 = preInsertsMach!![var1]
            if (var3 == null) var2.label else var2.interval(var3.maxStates)
        }
    }

    private fun crunch(var1: Int, var2: IntArray) {
        var var3: Int
        var3 = var2[var1]
        while (var3 >= 0 && var3 != var2[var3]) {
            var3 = var2[var3]
        }
        var2[var1] = var3
    }

    fun print(var1: LTSOutput) {
        var1.outln("PROCESS: $name")
        var1.outln("ALPHABET:")
        var var2: Enumeration<*> = alphabet.keys()
        var var3: String
        while (var2.hasMoreElements()) {
            var3 = var2.nextElement() as String
            var1.outln("\t" + alphabet[var3] + "\t" + var3)
        }
        var1.outln("EXPLICIT STATES:")
        var2 = explicit_states.keys()
        while (var2.hasMoreElements()) {
            var3 = var2.nextElement() as String
            var1.outln("\t" + explicit_states[var3] + "\t" + var3)
        }
        var1.outln("TRANSITIONS:")
        var2 = transitions.elements()
        while (var2.hasMoreElements()) {
            val var4 = var2.nextElement() as Transition
            var1.outln("\t" + var4)
        }
    }

    companion object {

        var output: LTSOutput? = null

        fun paramString(var0: Vector<Value>): String {
            val var1: Int = var0.size - 1
            val var2 = StringBuffer()
            val var3 = var0.elements()
            var2.append("(")
            for (var4 in 0..var1) {
                val var5 = var3.nextElement().toString()
                var2.append(var5)
                if (var4 < var1) {
                    var2.append(",")
                }
            }
            var2.append(")")
            return var2.toString()
        }

    }

}