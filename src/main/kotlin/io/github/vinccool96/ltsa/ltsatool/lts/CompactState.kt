package io.github.vinccool96.ltsa.ltsatool.lts

import io.github.vinccool96.ltsa.ltsatool.utils.toArrayOfNotNull
import io.github.vinccool96.ltsa.ltsatool.utils.toArrayOfNull
import java.io.PrintStream
import java.util.*


class CompactState : Automata {

    var name: String? = null

    var maxStates = 0

    override var alphabet: Array<String>

    var states: Array<EventState?>

    var endseq = -9999

    private var hasduplicates = false

    private var prop = false

    constructor() {
        alphabet = arrayOf()
        states = arrayOf()
    }

    constructor(var1: Int, var2: String?, var3: MyHashStack, var4: MyList, var5: Array<String>, var6: Int) {
        alphabet = var5
        name = var2
        maxStates = var1
        states = arrayOfNulls(maxStates)
        while (!var4.empty()) {
            val var7 = var4.getFrom()
            val var8 = if (var4.getTo() == null) -1 else var3[var4.getTo()]
            states[var7] = EventState.add(states[var7], EventState(var4.getAction(), var8))
            var4.next()
        }
        endseq = var6
    }

    fun reachable() {
        val var1 = EventState.reachable(states)
        val var2 = states
        maxStates = var1.size()
        states = arrayOfNulls(maxStates)
        for (var3 in var2.indices) {
            val var4 = var1[var3]
            if (var4 > -2) {
                states[var4] = EventState.renumberStates(var2[var3], var1)
            }
        }
        if (endseq > 0) {
            endseq = var1[endseq]
        }
    }

    fun removeNonDetTau() {
        if (this.hasTau()) {
            var var3: Int
            do {
                var var1 = false
                for (var2 in 0 until maxStates) {
                    states[var2] = EventState.remove(states[var2], EventState(0, var2))
                }
                val var4 = BitSet(maxStates)
                var3 = 1
                while (var3 < maxStates) {
                    if (EventState.hasOnlyTauAndAccept(states[var3], alphabet)) {
                        var4.set(var3)
                        var1 = true
                    }
                    ++var3
                }
                if (!var1) {
                    return
                }
                var3 = 0
                while (var3 < maxStates) {
                    if (!var4.get(var3)) {
                        states[var3] = EventState.addNonDetTau(states[var3], states, var4)
                    }
                    ++var3
                }
                var3 = maxStates
                reachable()
            } while (var3 != maxStates)
        }
    }

    fun removeDetCycles(var1: String) {
        val var2: Int = this.eventNo(var1)
        if (var2 < alphabet.size) {
            for (var3 in states.indices) {
                if (!EventState.hasNonDetEvent(states[var3], var2)) {
                    states[var3] = EventState.remove(states[var3], EventState(var2, var3))
                }
            }
        }
    }

    fun isSafetyOnly(): Boolean {
        var var1 = 0
        var var2 = 0
        for (var3 in 0 until maxStates) {
            if (EventState.isAccepting(states[var3], alphabet)) {
                ++var2
                if (EventState.isTerminal(var3, states[var3])) {
                    ++var1
                }
            }
        }
        return var1 == 1 && var2 == 1 || var2 == 0
    }

    fun makeSafety() {
        var var1 = -1
        var var2 = 0
        while (var2 < maxStates) {
            if (EventState.isAccepting(states[var2], alphabet)) {
                var1 = var2
                break
            }
            ++var2
        }
        if (var1 >= 0) {
            states[var1] = EventState.removeAccept(states[var1]!!)
        }
        var2 = 0
        while (var2 < maxStates) {
            EventState.replaceWithError(states[var2], var1)
            ++var2
        }
        reachable()
    }

    fun removeAcceptTau() {
        for (var1 in 1 until maxStates) {
            if (EventState.hasOnlyTauAndAccept(states[var1], alphabet)) {
                states[var1] = EventState.removeAccept(states[var1]!!)
            }
        }
    }

    fun hasERROR(): Boolean {
        for (var1 in 0 until maxStates) {
            if (EventState.hasState(states[var1], -1)) {
                return true
            }
        }
        return false
    }

    fun prefixLabels(var1: String) {
        name = "$var1:$name"
        for (var2 in 1 until alphabet.size) {
            val var3 = alphabet[var2]
            alphabet[var2] = "$var1.$var3"
        }
    }

    fun relabelDuplicates(): Boolean {
        return hasduplicates
    }

    fun relabel(var1: Relation) {
        hasduplicates = false
        if (var1.isRelation) {
            relational_relabel(var1)
        } else {
            functionalRelabel(var1)
        }
    }

    private fun relational_relabel(var1: Relation) {
        val var2 = Vector<Any>()
        val var3 = Relation()
        var2.setSize(alphabet.size)
        var var4: Int = alphabet.size
        var2.setElementAt(alphabet[0], 0)
        for (var5 in 1 until alphabet.size) {
            var var7 = var1[alphabet[var5]]
            if (var7 != null) {
                if (var7 is String) {
                    var2.setElementAt(var7, var5)
                } else {
                    val var13 = var7 as Vector<Any>
                    var2.setElementAt(var13.firstElement(), var5)
                    for (var14 in 1 until var13.size) {
                        var2.addElement(var13.elementAt(var14))
                        var3[var5] = var4
                        ++var4
                    }
                }
            } else {
                var var12: Int
                if (this.maximalPrefix(alphabet[var5], var1).also { var12 = it } >= 0) {
                    val var8 = alphabet[var5].substring(0, var12)
                    var7 = var1[var8]
                    if (var7 != null) {
                        if (var7 is String) {
                            var2.setElementAt(var7 + alphabet[var5].substring(var12), var5)
                        } else {
                            val var9 = var7 as Vector<Any>
                            var2.setElementAt(var9.firstElement() as String + alphabet[var5].substring(var12), var5)
                            for (var10 in 1 until var9.size) {
                                var2.addElement(var9.elementAt(var10) as String + alphabet[var5].substring(var12))
                                var3[var5] = var4
                                ++var4
                            }
                        }
                    } else {
                        var2.setElementAt(alphabet[var5], var5)
                    }
                } else {
                    var2.setElementAt(alphabet[var5], var5)
                }
            }
        }
        val var11 = arrayOfNulls<String>(var2.size)
        var2.copyInto(var11)
        alphabet = var11.toArrayOfNotNull()
        this.addTransitions(var3)
        checkDuplicates()
    }

    private fun functionalRelabel(var1: Hashtable<*, *>) {
        for (var2 in 1 until alphabet.size) {
            val var3 = var1[alphabet[var2]] as String?
            if (var3 != null) {
                alphabet[var2] = var3
            } else {
                alphabet[var2] = this.prefixLabelReplace(var2, var1)
            }
        }
        checkDuplicates()
    }

    private fun checkDuplicates() {
        val var1 = Hashtable<String, String>()
        for (var2 in 1 until alphabet.size) {
            if (var1.put(alphabet[var2], alphabet[var2]) != null) {
                hasduplicates = true
                crunchDuplicates()
            }
        }
    }

    private fun crunchDuplicates() {
        val var1 = Hashtable<String, Int>()
        val var2 = Hashtable<Int, Int>()
        var var3 = 0
        for (var4 in alphabet.indices) {
            if (var1.containsKey(alphabet[var4])) {
                var2[var4] = var1[alphabet[var4]]
            } else {
                var1[alphabet[var4]] = var3
                var2[var4] = var3
                ++var3
            }
        }
        val alphabet = arrayOfNulls<String>(var1.size)
        var var5: String
        var var6: Int
        val var7 = var1.keys()
        while (var7.hasMoreElements()) {
            var5 = var7.nextElement()
            var6 = var1[var5]!!
            alphabet[var6] = var5
        }
        this.alphabet = alphabet.toArrayOfNotNull()
        for (var8 in states.indices) {
            states[var8] = EventState.renumberEvents(states[var8], var2)
        }
    }

    fun hide(var1: Vector<String>): Vector<String> {
        val var2 = Vector<String>()
        for (var3 in 1 until alphabet.size) {
            if (!contains(alphabet[var3], var1)) {
                var2.addElement(alphabet[var3])
            }
        }
        return var2
    }

    fun expose(var1: Vector<String>) {
        val var2 = BitSet(alphabet.size)
        for (var3 in 1 until alphabet.size) {
            if (contains(alphabet[var3], var1)) {
                var2.set(var3)
            }
        }
        var2.set(0)
        doHiding(var2)
    }

    fun conceal(var1: Vector<String>) {
        val var2 = BitSet(alphabet.size)
        for (var3 in 1 until alphabet.size) {
            if (!contains(alphabet[var3], var1)) {
                var2.set(var3)
            }
        }
        var2.set(0)
        doHiding(var2)
    }

    private fun doHiding(var1: BitSet) {
        val var2 = 0
        val var3 = Hashtable<Int, Int>()
        val var4 = Vector<String>()
        var var5 = 0
        var var6 = 0
        while (var6 < alphabet.size) {
            if (!var1.get(var6)) {
                var3[var6] = var2
            } else {
                var4.addElement(alphabet[var6])
                var3[var6] = var5
                ++var5
            }
            ++var6
        }
        val alphabet = arrayOfNulls<String>(var4.size)
        var4.copyInto(alphabet)
        this.alphabet = alphabet.toArrayOfNotNull()
        var6 = 0
        while (var6 < states.size) {
            states[var6] = EventState.renumberEvents(states[var6], var3)
            ++var6
        }
    }

    fun isProperty(): Boolean {
        return prop
    }

    fun makeProperty() {
        endseq = -9999
        prop = true
        for (var1 in 0 until maxStates) {
            states[var1] = EventState.addTransToError(states[var1], alphabet.size)
        }
    }

    fun unMakeProperty() {
        endseq = -9999
        prop = false
        for (var1 in 0 until maxStates) {
            states[var1] = EventState.removeTransToError(states[var1])
        }
    }

    fun isNonDeterministic(): Boolean {
        for (var1 in 0 until maxStates) {
            if (EventState.hasNonDet(states[var1])) {
                return true
            }
        }
        return false
    }

    fun printAUT(var1: PrintStream) {
        var1.print("des(0,${ntransitions()},${maxStates})")
        for (var2 in states.indices) {
            EventState.printAUT(states[var2], var2, alphabet, var1)
        }
    }

    fun myclone(): CompactState {
        val var1 = CompactState()
        var1.name = name
        var1.endseq = endseq
        var1.prop = prop
        var1.alphabet = alphabet.copyOf()
        var1.maxStates = maxStates
        var1.states = arrayOfNulls(maxStates)
        var var2 = 0
        while (var2 < maxStates) {
            var1.states[var2] = EventState.union(var1.states[var2], states[var2])
            ++var2
        }
        return var1
    }

    fun ntransitions(): Int {
        var var1 = 0
        for (element in states) {
            var1 += EventState.count(element)
        }
        return var1
    }

    fun hasTau(): Boolean {
        for (element in states) {
            if (EventState.hasTau(element)) {
                return true
            }
        }
        return false
    }

    private fun prefixLabelReplace(var1: Int, var2: Hashtable<*, *>): String {
        val var3 = maximalPrefix(alphabet[var1], var2)
        return if (var3 < 0) {
            alphabet[var1]
        } else {
            val var4 = alphabet[var1].substring(0, var3)
            val var5 = var2[var4] as String?
            if (var5 == null) alphabet[var1] else var5 + alphabet[var1].substring(var3)
        }
    }

    private fun maximalPrefix(var1: String, var2: Hashtable<*, *>): Int {
        val var3 = var1.lastIndexOf(46.toChar())
        return if (var3 < 0) {
            var3
        } else {
            if (var2.containsKey(var1.substring(0, var3))) var3 else maximalPrefix(var1.substring(0, var3), var2)
        }
    }

    fun isErrorTrace(var1: Vector<String>): Boolean {
        var var2 = false
        var var3 = 0
        while (var3 < maxStates && !var2) {
            if (EventState.hasState(states[var3], -1)) {
                var2 = true
            }
            ++var3
        }
        return if (!var2) false else isTrace(var1, 0, 0)
    }

    private fun isTrace(var1: Vector<String>, var2: Int, var3: Int): Boolean {
        return if (var2 < var1.size) {
            val var4 = var1.elementAt(var2)
            val var5 = eventNo(var4)
            if (var5 < alphabet.size) {
                if (EventState.hasEvent(states[var3], var5)) {
                    val var6 = EventState.nextState(states[var3], var5)
                    for (var7 in var6!!.indices) {
                        if (isTrace(var1, var2 + 1, var6[var7])) {
                            return true
                        }
                    }
                    return false
                }
                if (var5 != 0) {
                    return false
                }
            }
            isTrace(var1, var2 + 1, var3)
        } else {
            var3 == -1
        }
    }

    private fun eventNo(var1: String): Int {
        var var2 = 0
        while (var2 < alphabet.size && var1 != alphabet[var2]) {
            ++var2
        }
        return var2
    }

    fun addAccess(var1: Vector<String>) {
        val var2: Int = var1.size
        if (var2 != 0) {
            var var3 = "{"
            val var4 = arrayOfNulls<CompactState>(var2)
            val var5 = var1.elements()
            var var6 = 0
            while (var5.hasMoreElements()) {
                val var7 = var5.nextElement()
                var3 += var7
                var4[var6] = myclone()
                var4[var6]!!.prefixLabels(var7)
                ++var6
                if (var6 < var2) {
                    var3 = "$var3,"
                }
            }
            name = "$var3}::$name"
            val var10: Int = alphabet.size - 1
            val alphabet = arrayOfNulls<String>(var10 * var2 + 1)
            alphabet[0] = "tau"
            var var9: Int
            var var8 = 0
            while (var8 < var2) {
                var9 = 1
                while (var9 < var4[var8]!!.alphabet.size) {
                    alphabet[var10 * var8 + var9] = var4[var8]!!.alphabet[var9]
                    ++var9
                }
                ++var8
            }
            this.alphabet = alphabet.toArrayOfNotNull()
            var8 = 1
            while (var8 < var2) {
                var9 = 0
                while (var9 < maxStates) {
                    EventState.offsetEvents(var4[var8]!!.states[var9], var10 * var8)
                    states[var9] = EventState.union(states[var9], var4[var8]!!.states[var9])
                    ++var9
                }
                ++var8
            }
        }
    }

    private fun addTransitions(var1: Relation) {
        for (var2 in states.indices) {
            val var3 = EventState.newTransitions(states[var2], var1)
            if (var3 != null) {
                states[var2] = EventState.union(states[var2], var3)
            }
        }
    }

    fun hasLabel(var1: String): Boolean {
        for (element in alphabet) {
            if (var1 == element) {
                return true
            }
        }
        return false
    }

    fun usesLabel(var1: String): Boolean {
        return if (!hasLabel(var1)) {
            false
        } else {
            val var2 = eventNo(var1)
            for (element in states) {
                if (EventState.hasEvent(element, var2)) {
                    return true
                }
            }
            false
        }
    }

    fun isSequential(): Boolean {
        return endseq >= 0
    }

    fun isEnd(): Boolean {
        return maxStates == 1 && endseq == 0
    }

    fun expandSequential(var1: Hashtable<Int, CompactState>) {
        val var2: Int = var1.size
        val var3 = arrayOfNulls<CompactState>(var2 + 1)
        val var4 = IntArray(var2 + 1)
        var3[0] = this
        var var5 = 1
        val var6 = var1.keys()
        while (var6.hasMoreElements()) {
            val var7 = var6.nextElement()
            val var8 = var1[var7]
            var3[var5] = var8
            var4[var5] = var7
            ++var5
        }
        alphabet = sharedAlphabet(var3.toArrayOfNotNull())
        for (var10 in 1 until var3.size) {
            val var11 = var4[var10]
            for (var9 in 0 until var3[var10]!!.states.size) {
                states[var11 + var9] = var3[var10]!!.states[var9]
            }
        }
    }

    fun offsetSeq(var1: Int, var2: Int) {
        for (element in states) {
            EventState.offsetSeq(var1, endseq, var2, element)
        }
    }

    private fun encode(var1: Int): ByteArray {
        var var4 = var1
        val var2 = ByteArray(4)
        for (var3 in 0..3) {
            var2[var3] = (var2[var3].toInt() or var4.toByte().toInt()).toByte()
            var4 = var4 ushr 8
        }
        return var2
    }

    private fun decode(var1: ByteArray): Int {
        var var2 = 0
        for (var3 in 3 downTo 0) {
            var2 = var2 or (var1[var3].toInt() and 255)
            if (var3 > 0) {
                var2 = var2 shl 8
            }
        }
        return var2
    }

    val alphabetV: Vector<String>
        get() {
            val var1 = Vector<String>(alphabet.size - 1)
            for (var2 in 1 until alphabet.size) {
                var1.add(alphabet[var2])
            }
            return var1
        }

    override fun getTransitions(var1: ByteArray?): MyList {
        val var2 = MyList()
        val var3: Int = if (var1 == null) {
            -1
        } else {
            decode(var1)
        }
        return if (var3 in 0 until maxStates) {
            if (states[var3] != null) {
                val var4: Enumeration<*> = states[var3]!!.elements()
                while (var4.hasMoreElements()) {
                    val var5 = var4.nextElement() as EventState
                    var2.add(var3, encode(var5.next), var5.event)
                }
            }
            var2
        } else {
            var2
        }
    }

    override val violatedProperty: String? = null

    override fun getTraceToState(var1: ByteArray, var2: ByteArray): Vector<String> {
        val var3 = EventState(0, 0)
        EventState.search(var3, states, decode(var1), decode(var2), -123456)
        return EventState.getPath(var3.path, this.alphabet.toArrayOfNull())
    }

    override fun END(var1: ByteArray): Boolean {
        return decode(var1) == endseq
    }

    override fun isAccepting(var1: ByteArray): Boolean {
        return this.isAccepting(decode(var1))
    }

    override fun START(): ByteArray {
        return encode(0)
    }

    override fun setStackChecker(var1: StackCheck) {}

    override val isPartialOrder: Boolean = false

    override fun disablePartialOrder() {}

    override fun enablePartialOrder() {}

    fun isAccepting(var1: Int): Boolean {
        return if (var1 in 0 until maxStates) EventState.isAccepting(states[var1], alphabet) else false
    }

    fun accepting(): BitSet {
        val var1 = BitSet()
        for (var2 in 0 until maxStates) {
            if (this.isAccepting(var2)) {
                var1.set(var2)
            }
        }
        return var1
    }

    companion object {

        fun contains(var0: String, var1: Vector<String>): Boolean {
            val var2 = var1.elements()
            var var3: String
            do {
                if (!var2.hasMoreElements()) {
                    return false
                }
                var3 = var2.nextElement()
            } while (var3 != var0 && !isPrefix(var3, var0))
            return true
        }

        private fun isPrefix(var0: String, var1: String): Boolean {
            val var2 = var1.lastIndexOf(46.toChar())
            return if (var2 < 0) {
                false
            } else {
                if (var0 == var1.substring(0, var2)) true else isPrefix(var0, var1.substring(0, var2))
            }
        }

        fun sequentialCompose(var0: Vector<CompactState>?): CompactState? {
            return if (var0 == null) {
                null
            } else if (var0.isEmpty()) {
                null
            } else if (var0.size == 1) {
                var0.elementAt(0)
            } else {
                val var1 = var0.toTypedArray()
                val var2 = CompactState()
                var2.alphabet = sharedAlphabet(var1)
                var2.maxStates = seqSize(var1)
                var2.states = arrayOfNulls(var2.maxStates)
                var var3 = 0
                for (var4 in var1.indices) {
                    val var5 = var4 == var1.size - 1
                    copyOffset(var3, var2.states, var1[var4], var5)
                    if (var5) {
                        var2.endseq = var1[var4]!!.endseq + var3
                    }
                    var3 += var1[var4]!!.states.size
                }
                var2
            }
        }

        private fun seqSize(var0: Array<CompactState>): Int {
            var var1 = 0
            for (var2 in var0.indices) {
                var1 += var0[var2].states.size
            }
            return var1
        }

        private fun copyOffset(var0: Int, var1: Array<EventState?>, var2: CompactState, var3: Boolean) {
            for (var4 in 0 until var2.states.size) {
                if (!var3) {
                    var1[var4 + var0] =
                            EventState.offsetSeq(var0, var2.endseq, var2.maxStates + var0, var2.states[var4])
                } else {
                    var1[var4 + var0] = EventState.offsetSeq(var0, var2.endseq, var2.endseq + var0, var2.states[var4])
                }
            }
        }

        private fun sharedAlphabet(var0: Array<CompactState>): Array<String> {
            val var1 = Counter(0)
            val var2 = Hashtable<String, Int>()
            for (var3 in var0.indices) {
                for (var4 in 0 until var0[var3].alphabet.size) {
                    if (!var2.containsKey(var0[var3].alphabet[var4])) {
                        var2[var0[var3].alphabet[var4]] = var1.label
                    }
                }
            }
            val var9 = arrayOfNulls<String>(var2.size)
            var var5: String
            var var6: Int
            val var10 = var2.keys()
            while (var10.hasMoreElements()) {
                var5 = var10.nextElement() as String
                var6 = var2[var5]!!
                var9[var6] = var5
            }
            for (var11 in var0.indices) {
                var6 = 0
                while (var6 < var0[var11].maxStates) {
                    var var7 = var0[var11].states[var6]
                    while (var7 != null) {
                        var var8 = var7
                        var7.event = var2[var0[var11].alphabet[var7.event]]!!
                        while (var8!!.nondet != null) {
                            var8.nondet!!.event = var8.event
                            var8 = var8.nondet
                        }
                        var7 = var7.list
                    }
                    ++var6
                }
            }
            return var9.toArrayOfNotNull()
        }

    }

}