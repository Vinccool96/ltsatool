package io.github.vinccool96.ltsa.ltsatool.lts

import io.github.vinccool96.ltsa.ltsatool.lts.ltl.FluentTrace
import io.github.vinccool96.ltsa.ltsatool.utils.toArrayOfNotNull
import java.util.*
import kotlin.math.abs

class Analyser(private var cs: CompositeState, private val output: LTSOutput, private val eman: EventManager?,
        var4: Boolean) : Animator, Automata {

    private var sm: Array<CompactState>

    private val realAlphabet: Hashtable<String, BitSet> = Hashtable()

    private var actionMap: Hashtable<String, Int> = Hashtable()

    private var actionCount: IntArray

    private var actionName: Array<String>

    private var Nmach = 0

    private var Mbase: IntArray

    private var analysed: MyHashStack? = null

    private var stateNo = 0

    private var stateCount = 0

    private val violated = BooleanArray(cs.machines.size)

    private var deadlockDetected = false

    private var lowpriority = false

    private var priorLabels: Vector<String>? = null

    private var highAction: BitSet? = null

    private var acceptEvent = -1

    private var asteriskEvent = -1

    private val visible: BitSet

    private val coder: StateCodec

    private var canTerminate = false

    private var partial: PartialOrder? = null

    private var compTrans: MyList? = null

    private var endSequence = -99999

    // TODO: Rename to errorTrace
    var errorTrace: List<String>? = null
        private set

    var errorMachine = 0

    var savedPartial: PartialOrder? = null

    private var menuAlpha: Array<String>? = null

    private var actionToIndex: Hashtable<Int, Int>? = null

    private var indexToAction: Hashtable<Int, Int>? = null

    private var currentA: IntArray? = null

    @Volatile
    private var choices: MutableList<IntArray>? = null

    private var errorState = false

    private var _replay: Enumeration<String>? = null

    private var _replayAction: String? = null

    var theChoice = 0

    val rand = Random()

    constructor(cs: CompositeState, output: LTSOutput, eman: EventManager?) : this(cs, output, eman, false)

    init {
        if (cs.priorityLabels != null) {
            lowpriority = cs.priorityIsLow
            priorLabels = cs.priorityLabels
            highAction = BitSet()
        }
        val sm = arrayOfNulls<CompactState>(cs.machines.size)
        val var5 = cs.machines.elements()
        var var6 = 0
        while (var5.hasMoreElements()) {
            sm[var6] = (var5.nextElement()).myclone()
            ++var6
        }
        this.sm = sm.toArrayOfNotNull()
        Nmach = this.sm.size
        output.outln("Composition:")
        output.out(this.cs.name + " = ")
        var6 = 0
        while (var6 < this.sm.size) {
            output.out(this.sm[var6].name!!)
            if (var6 < this.sm.size - 1) {
                output.out(" || ")
            }
            ++var6
        }
        output.outln("")
        if (priorLabels != null) {
            if (lowpriority) {
                output.out("\t>> ")
            } else {
                output.out("\t<< ")
            }
            output.outln(Alphabet(cs.priorityLabels!!).toString())
        }
        Mbase = IntArray(Nmach)
        output.outln("State Space:")
        var6 = 0
        while (var6 < this.sm.size) {
            output.out(" " + this.sm[var6].maxStates + " ")
            if (var6 < this.sm.size - 1) {
                output.out("*")
            }
            Mbase[var6] = this.sm[var6].maxStates
            ++var6
        }
        coder = StateCodec(Mbase)
        output.outln("= 2 ** " + coder.bits())
        val var13 = HashSet<String>()
        val var7 = HashSet<String>()
        val var8 = Counter(0)
        var var10: Int
        var var9 = 0
        while (var9 < this.sm.size) {
            var10 = 0
            while (var10 < this.sm[var9].alphabet.size) {
                if (this.sm[var9].endseq > 0) {
                    var13.add(this.sm[var9].alphabet[var10])
                } else {
                    var7.add(this.sm[var9].alphabet[var10])
                }
                var var11 = realAlphabet[this.sm[var9].alphabet[var10]]
                if (var11 == null) {
                    var11 = BitSet()
                    var11.set(var9)
                    val var12 = this.sm[var9].alphabet[var10]
                    realAlphabet[var12] = var11
                    actionMap[var12] = var8.label
                } else {
                    var11.set(var9)
                }
                ++var10
            }
            ++var9
        }
        canTerminate = var13.containsAll(var7)
        val actionName = arrayOfNulls<String>(realAlphabet.size)
        actionCount = IntArray(realAlphabet.size)
        for (var14 in realAlphabet.keys()) {
            val var15 = realAlphabet[var14]!!
            val var16 = actionMap[var14]!!
            actionName[var16] = var14
            actionCount[var16] = this.countSet(var15)
            if (var14[0] == '@') {
                acceptEvent = var16
            } else if (var14 == "*" && !var4) {
                asteriskEvent = var16
            }
            if (highAction != null) {
                if (!lowpriority) {
                    if (CompactState.contains(var14, priorLabels!!)) {
                        highAction!!.set(var16)
                    }
                } else if (!CompactState.contains(var14, priorLabels!!)) {
                    highAction!!.set(var16)
                }
            }
        }
        this.actionName = actionName.toArrayOfNotNull()
        if (highAction != null) {
            if (lowpriority) {
                highAction!!.set(0)
            } else {
                highAction!!.clear(0)
            }
            if (acceptEvent > 0) {
                highAction!!.clear(acceptEvent)
            }
        }
        actionCount[0] = 0
        var9 = 0
        while (var9 < sm.size) {
            var10 = 0
            while (var10 < this.sm[var9].maxStates) {
                var var17 = this.sm[var9].states[var10]
                while (var17 != null) {
                    var var18 = var17
                    var17.machine = var9
                    var17.event = (actionMap[this.sm[var9].alphabet[var17.event]] as Int?)!!
                    while (var18!!.nondet != null) {
                        var18.nondet!!.event = var18.event
                        var18.nondet!!.machine = var18.machine
                        var18 = var18.nondet
                    }
                    var17 = var17.list
                }
                ++var10
            }
            ++var9
        }
        visible = BitSet(actionName.size)
        var9 = 1
        while (var9 < actionName.size) {
            if (cs.hidden == null) {
                visible.set(var9)
            } else if (cs.exposeNotHide) {
                if (CompactState.contains(this.actionName[var9], cs.hidden!!)) {
                    visible.set(var9)
                }
            } else if (!CompactState.contains(this.actionName[var9], cs.hidden!!)) {
                visible.set(var9)
            }
            ++var9
        }
    }

    fun compose(): CompactState {
        return privateCompose(true)
    }

    fun composeNoHide(): CompactState {
        return privateCompose(false)
    }

    private fun privateCompose(var1: Boolean): CompactState {
        output.outln("Composing...")
        val var2 = System.currentTimeMillis()
        val var4: Int = this.newStateCompose()
        val var5 = CompactState(stateCount, cs.name, analysed!!, compTrans!!, actionName, endSequence)
        if (var1 && cs.hidden != null) {
            if (!cs.exposeNotHide) {
                var5.conceal(cs.hidden!!)
            } else {
                var5.expose(cs.hidden!!)
            }
        }
        val var6 = System.currentTimeMillis()
        this.outStatistics(stateCount, compTrans!!.size())
        output.outln("Composed in " + (var6 - var2) + "ms")
        analysed = null
        compTrans = null
        return var5
    }

    fun analyse(var1: FluentTrace) {
        output.outln("Analysing...")
        System.gc()
        val var2 = System.currentTimeMillis()
        val var4: Int = this.newStateAnalyse(coder.zero(), null)
        val var5 = System.currentTimeMillis()
        if (var4 == 1) {
            output.outln("Trace to DEADLOCK:")
            var1.print(output, errorTrace, true)
        } else if (var4 == 2) {
            output.outln("Trace to property violation in " + sm[errorMachine].name + ":")
            var1.print(output, errorTrace, true)
        } else {
            output.outln("No deadlocks/errors")
        }
        output.outln("Analysed in: " + (var5 - var2) + "ms")
    }

    fun analyse() {
        output.outln("Analysing...")
        System.gc()
        val var1 = System.currentTimeMillis()
        val var3: Int = this.newStateAnalyse(coder.zero(), null as ByteArray?)
        val var4 = System.currentTimeMillis()
        if (var3 == 1) {
            output.outln("Trace to DEADLOCK:")
            this.printPath(errorTrace!! as LinkedList<String>)
        } else if (var3 == 2) {
            output.outln("Trace to property violation in " + sm[errorMachine].name + ":")
            this.printPath(errorTrace!! as LinkedList<String>)
        } else {
            output.outln("No deadlocks/errors")
        }
        output.outln("Analysed in: " + (var4 - var1) + "ms")
    }

    private fun countSet(var1: BitSet): Int {
        var var2 = 0
        for (var3 in 0 until var1.size()) {
            if (var1[var3]) {
                ++var2
            }
        }
        return var2
    }

    private fun isEND(var1: IntArray): Boolean {
        return if (!canTerminate) {
            false
        } else {
            for (var2 in 0 until Nmach) {
                if (sm[var2].endseq >= 0 && sm[var2].endseq != var1[var2]) {
                    return false
                }
            }
            true
        }
    }

    private fun printState(var1: IntArray) {
        output.out("[")
        for (var2 in var1.indices) {
            output.out("" + var1[var2])
            if (var2 < var1.size - 1) {
                output.out(",")
            }
        }
        output.out("]")
    }

    private fun myclone(var1: IntArray): IntArray {
        val var2 = IntArray(var1.size)
        for (var3 in var1.indices) {
            var2[var3] = var1[var3]
        }
        return var2
    }

    fun eligibleTransitions(var1: IntArray): List<IntArray>? {
        var var2: ArrayList<IntArray>? = null
        if (partial != null && (asteriskEvent <= 0 || !EventState.hasEvent(sm[Nmach - 1].states[var1[Nmach - 1]],
                        asteriskEvent))) {
            val var3 = partial!!.transitions(var1)
            if (var3 != null) {
                return var3
            }
        }
        val var12 = myclone(actionCount)
        val var4 = arrayOfNulls<EventState>(actionCount.size)
        var var5 = 0
        var var6 = 0
        var var7: Int
        var var10002: Int
        var7 = 0
        while (var7 < Nmach) {
            var var8 = sm[var7].states[var1[var7]]
            while (var8 != null) {
                var8.path = var4[var8.event]
                var4[var8.event] = var8
                var10002 = var12[var8.event]--
                if (var8.event != 0 && var12[var8.event] == 0) {
                    ++var5
                    if (highAction != null && highAction!![var8.event] && var8.event != asteriskEvent) {
                        ++var6
                    }
                }
                var8 = var8.list
            }
            ++var7
        }
        return if (var5 == 0 && var4[0] == null) {
            null
        } else {
            var7 = 1
            val var13 = ArrayList<IntArray>(8)
            if (var4[0] != null) {
                val var9 = highAction != null && highAction!![0]
                if (var9 || var6 == 0) {
                    computeTauTransitions(var4[0], var1, var13)
                }
                if (var9) {
                    ++var6
                }
            }
            while (var5 > 0) {
                --var5
                while (var12[var7] > 0) {
                    ++var7
                }
                if (var6 <= 0 || highAction!![var7] || var7 == acceptEvent) {
                    var var14 = var4[var7]
                    var var10: Boolean
                    var10 = false
                    while (var14 != null) {
                        if (var14.nondet != null) {
                            var10 = true
                            break
                        }
                        var14 = var14.path
                    }
                    var14 = var4[var7]
                    if (var10) {
                        if (var7 != asteriskEvent) {
                            computeNonDetTransitions(var14, var1, var13)
                        } else {
                            computeNonDetTransitions(var14, var1, ArrayList<IntArray>(4).also {
                                var2 = it
                            })
                        }
                    } else {
                        val var11 = myclone(var1)
                        var11[Nmach] = var7
                        while (var14 != null) {
                            var11[var14.machine] = var14.next
                            var14 = var14.path
                        }
                        if (var7 != asteriskEvent) {
                            var13.add(var11)
                        } else {
                            var2 = ArrayList<IntArray>(1)
                            var2!!.add(var11)
                        }
                    }
                }
                var10002 = var12[var7]++
            }
            if (asteriskEvent < 0) {
                var13
            } else {
                mergeAsterisk(var13, var2)
            }
        }
    }

    private fun computeTauTransitions(var1: EventState?, var2: IntArray, var3: MutableList<IntArray>) {
        var var4 = var1
        while (var4 != null) {
            var var5 = var4
            while (var5 != null) {
                val var6 = myclone(var2)
                var6[var5.machine] = var5.next
                var6[Nmach] = 0
                var3.add(var6)
                var5 = var5.nondet
            }
            var4 = var4.path
        }
    }

    private fun computeNonDetTransitions(var1: EventState?, var2: IntArray, var3: MutableList<IntArray>) {
        var var4 = var1
        while (var4 != null) {
            val var5 = myclone(var2)
            var5[var4.machine] = var4.next
            if (var1!!.path != null) {
                computeNonDetTransitions(var1.path, var5, var3)
            } else {
                var5[Nmach] = var1.event
                var3.add(var5)
            }
            var4 = var4.nondet
        }
    }

    fun mergeAsterisk(var1: List<IntArray>?, var2: List<IntArray>?): List<IntArray>? {
        return if (var1 != null && var2 != null) {
            if (var1.size == 0) {
                null
            } else {
                var var3: IntArray
                val var4: Iterator<IntArray>
                if (var2.size == 1) {
                    var3 = var2[0]
                    var4 = var1.iterator()
                    while (var4.hasNext()) {
                        val var8 = var4.next()
                        if (!visible[var8[Nmach]]) {
                            var8[Nmach - 1] = var3[Nmach - 1]
                        }
                    }
                    var1
                } else {
                    var4 = var2.iterator()
                    val var5 = ArrayList<IntArray>()
                    while (var4.hasNext()) {
                        var3 = var4.next()
                        var var7: IntArray
                        val var6 = var1.iterator()
                        while (var6.hasNext()) {
                            var7 = var6.next()
                            if (!visible[var7[Nmach]]) {
                                var7[Nmach - 1] = var3[Nmach - 1]
                            }
                            var5.add(myclone(var7))
                        }
                    }
                    var5
                }
            }
        } else {
            var1
        }
    }

    private fun outStatistics(var1: Int, var2: Int) {
        val var3 = Runtime.getRuntime()
        output.outln(
                "-- States: " + var1 + " Transitions: " + var2 + " Memory used: " + (var3.totalMemory() - var3.freeMemory()) / 1000L + "K")
    }

    private fun newStateCompose(): Int {
        System.gc()
        analysed = MyHashStack(100001)
        if (partialOrderReduction) {
            partial = PartialOrder(realAlphabet, actionName, sm, StackChecker(coder, analysed!!), cs.hidden,
                    cs.exposeNotHide, preserveObsEquiv, highAction)
        }
        compTrans = MyList()
        stateCount = 0
        analysed!!.pushPut(coder.zero())
        while (true) {
            label67@ while (!analysed!!.empty()) {
                if (analysed!!.marked()) {
                    analysed!!.pop()
                } else {
                    val var1 = coder.decode(analysed!!.peek())
                    analysed!!.mark(stateCount++)
                    if (stateCount % 10000 == 0) {
                        output.out("Depth ${analysed!!.getDepth} ")
                        outStatistics(stateCount, compTrans!!.size())
                    }
                    val var2 = eligibleTransitions(var1)
                    if (var2 == null) {
                        if (!isEND(var1)) {
                            if (!deadlockDetected) {
                                output.outln("  potential DEADLOCK")
                            }
                            deadlockDetected = true
                        } else if (endSequence < 0) {
                            endSequence = stateCount - 1
                        } else {
                            analysed!!.mark(endSequence)
                            --stateCount
                        }
                    } else {
                        val var3 = var2.iterator()
                        while (true) {
                            while (true) {
                                if (!var3.hasNext()) {
                                    continue@label67
                                }
                                val var4 = var3.next()
                                val var5 = coder.encode(var4)
                                compTrans!!.add(stateCount - 1, var5, var4[Nmach])
                                if (var5 == null) {
                                    var var6 = 0
                                    while (var4[var6] >= 0) {
                                        ++var6
                                    }
                                    if (!violated[var6]) {
                                        output.outln("  property " + sm[var6].name + " violation.")
                                    }
                                    violated[var6] = true
                                } else if (!analysed!!.containsKey(var5)) {
                                    analysed!!.pushPut(var5)
                                }
                            }
                        }
                    }
                }
            }
            return 0
        }
    }

    private fun printPath(var1: LinkedList<String>) {
        val var2 = var1.iterator()
        while (var2.hasNext()) {
            output.outln("\t" + var2.next())
        }
    }

    private fun newStateAnalyse(var1: ByteArray, var2: ByteArray?): Int {
        var var6: ByteArray = var1
        stateCount = 0
        var var3 = 0
        val var4 = MyHashQueue(100001)
        if (partialOrderReduction) {
            partial = PartialOrder(realAlphabet, actionName, sm, StackChecker(coder, var4), cs.hidden, cs.exposeNotHide,
                    false, highAction)
        }
        var4.addPut(var6, 0, null)
        var var7: MyHashQueueEntry? = null
        while (true) {
            while (!var4.empty()) {
                var7 = var4.peek()
                var6 = var7!!.key
                val var8 = coder.decode(var6)
                ++stateCount
                if (stateCount % 10000 == 0) {
                    output.out("Depth " + var4.depth(var7) + " ")
                    outStatistics(stateCount, var3)
                }
                val var9 = eligibleTransitions(var8)
                var4.pop()
                if (var9 != null) {
                    val var10 = var9.iterator()
                    while (var10.hasNext()) {
                        val var11 = var10.next()
                        val var12 = coder.encode(var11)
                        ++var3
                        if (var12 == null || StateCodec.equals(var12, var2)) {
                            output.out("Depth " + var4.depth(var7) + " ")
                            outStatistics(stateCount, var3)
                            if (var12 == null) {
                                var var13 = 0
                                while (var11[var13] >= 0) {
                                    ++var13
                                }
                                errorMachine = var13
                            }
                            errorTrace = var4.getPath(var7, actionName)
                            (errorTrace!! as LinkedList<String>).add(actionName[var11[Nmach]])
                            return if (var12 == null) 2 else 3
                        }
                        if (!var4.containsKey(var12)) {
                            var4.addPut(var12, var11[Nmach], var7)
                        }
                    }
                } else if (!isEND(var8)) {
                    output.out("Depth " + var4.depth(var7) + " ")
                    outStatistics(stateCount, var3)
                    errorTrace = var4.getPath(var7, actionName)
                    return 1
                }
            }
            output.out("Depth " + var4.depth(var7!!) + " ")
            outStatistics(stateCount, var3)
            return 0
        }
    }

    override val alphabet: Array<String>
        get() {
            return actionName
        }

    override fun getTransitions(var1: ByteArray?): MyList {
        val var2: List<IntArray>? = eligibleTransitions(coder.decode(var1!!))
        val var3 = MyList()
        return if (var2 == null) {
            var3
        } else {
            var var5: IntArray
            var var6: ByteArray?
            val var4 = var2.iterator()
            while (var4.hasNext()) {
                var5 = var4.next()
                var6 = coder.encode(var5)
                if (var6 == null) {
                    var var7 = 0
                    while (var5[var7] >= 0) {
                        ++var7
                    }
                    errorMachine = var7
                }
                var3.add(0, var6, var5[Nmach])
            }
            var3
        }
    }

    override fun isAccepting(var1: ByteArray): Boolean {
        return if (acceptEvent < 0) {
            false
        } else {
            val var2 = coder.decode(var1)
            EventState.hasEvent(sm[Nmach - 1].states[var2[Nmach - 1]], acceptEvent)
        }
    }

    override val violatedProperty: String?
        get() {
            return sm[errorMachine].name
        }

    override fun getTraceToState(var1: ByteArray, var2: ByteArray): Vector<String>? {
        return if (StateCodec.equals(var1, var2)) {
            Vector()
        } else {
            val var3 = newStateAnalyse(var1, var2)
            if (var3 == 3) {
                val var4 = Vector<String>()
                var4.addAll(errorTrace!!)
                var4
            } else {
                null
            }
        }
    }

    override fun END(var1: ByteArray): Boolean {
        return isEND(coder.decode(var1))
    }

    override fun START(): ByteArray {
        return coder.zero()
    }

    override fun setStackChecker(var1: StackCheck) {
        if (partialOrderReduction) {
            partial = PartialOrder(realAlphabet, actionName, sm, StackChecker(coder, var1), cs.hidden, cs.exposeNotHide,
                    false, highAction)
        }
    }

    override val isPartialOrder: Boolean
        get() {
            return partialOrderReduction
        }

    override fun disablePartialOrder() {
        savedPartial = partial
        partial = null
    }

    override fun enablePartialOrder() {
        partial = savedPartial
    }

    private fun getMenuHash() {
        actionToIndex = Hashtable()
        indexToAction = Hashtable()
        for (var1 in 1 until menuAlpha!!.size) {
            val var3 = actionMap[menuAlpha!![var1]]
            actionToIndex!![var3] = var1
            indexToAction!![var1] = var3
        }
    }

    private fun getMenu(var1: Vector<String>?) {
        if (var1 != null) {
            val var2 = Vector<String>()
            val var3 = var1.elements()
            while (var3.hasMoreElements()) {
                val var4 = var3.nextElement()
                if (realAlphabet.containsKey(var4)) {
                    var2.addElement(var4)
                }
            }
            val menuAlpha = arrayOfNulls<String>(var2.size + 1)
            menuAlpha[0] = "tau"
            for (var5 in 1 until menuAlpha.size) {
                menuAlpha[var5] = var2.elementAt(var5 - 1)
            }
            this.menuAlpha = menuAlpha.toArrayOfNotNull()
        } else {
            menuAlpha = actionName
        }
        getMenuHash()
    }

    private fun menuActions(): BitSet {
        val var1 = BitSet(menuAlpha!!.size)
        if (choices != null) {
            val var2: Iterator<*> = choices!!.iterator()
            while (var2.hasNext()) {
                val var4 = (var2.next() as IntArray)[Nmach]
                val var5 = actionToIndex!![var4]
                if (var5 != null) {
                    var1.set(var5)
                }
            }
        }
        return var1
    }

    private fun allActions(): BitSet {
        val var1 = BitSet(actionCount.size)
        if (choices != null) {
            val var2 = choices!!.iterator()
            while (var2.hasNext()) {
                var1.set(var2.next()[Nmach])
            }
        }
        return var1
    }

    override fun initialise(var1: Vector<String>): BitSet {
        choices = eligibleTransitions(coder.decode(coder.zero()).also { currentA = it }) as MutableList<IntArray>
        eman?.post(LTSEvent(0, currentA))
        getMenu(var1)
        if (cs.errorTrace != null) {
            _replay = (cs.errorTrace as Vector<String>).elements()
            if (_replay!!.hasMoreElements()) {
                _replayAction = _replay!!.nextElement() as String
            }
        }
        return menuActions()
    }

    override fun singleStep(): BitSet? {
        return if (errorState) {
            null
        } else {
            if (nonMenuChoice()) {
                currentA = this.step(randomNonMenuChoice())
                if (errorState) {
                    return null
                }
                choices = eligibleTransitions(currentA!!) as MutableList<IntArray>
            }
            menuActions()
        }
    }

    override fun menuStep(var1: Int): BitSet? {
        return if (errorState) {
            null
        } else {
            theChoice = indexToAction!![var1]!!
            currentA = this.step(theChoice)
            if (errorState) {
                null
            } else {
                choices = eligibleTransitions(currentA!!) as MutableList<IntArray>
                menuActions()
            }
        }
    }

    override fun actionChosen(): Int {
        return theChoice
    }

    override fun actionNameChosen(): String {
        return actionName[theChoice]
    }

    override fun nonMenuChoice(): Boolean {
        return if (errorState) {
            false
        } else {
            val var1 = allActions()
            for (var2 in 0 until var1.size()) {
                if (var1[var2] && !actionToIndex!!.containsKey(var2)) {
                    theChoice = var2
                    return true
                }
            }
            false
        }
    }

    private fun randomNonMenuChoice(): Int {
        val var1 = allActions()
        val var2 = ArrayList<Int>(8)
        var var3 = 0
        while (var3 < var1.size()) {
            val var4 = var3
            if (var1[var3] && !actionToIndex!!.containsKey(var4)) {
                var2.add(var4)
            }
            ++var3
        }
        var3 = abs(rand.nextInt()) % var2.size
        theChoice = var2[var3]
        return theChoice
    }

    override fun traceChoice(): Boolean {
        return if (errorState) {
            false
        } else if (_replay == null) {
            false
        } else {
            if (_replayAction != null) {
                val var1 = actionMap[_replayAction] as Int
                val var2 = allActions()
                if (var2[var1]) {
                    theChoice = var1
                    return true
                }
            }
            false
        }
    }

    override val hasErrorTrace: Boolean
        get() {
            return cs.errorTrace != null
        }

    override fun traceStep(): BitSet? {
        return if (errorState) {
            null
        } else {
            if (traceChoice()) {
                currentA = this.step(theChoice)
                if (errorState) {
                    return null
                }
                choices = eligibleTransitions(currentA!!) as MutableList<IntArray>
                _replayAction = if (_replay!!.hasMoreElements()) {
                    _replay!!.nextElement()
                } else {
                    null
                }
            }
            menuActions()
        }
    }

    override val isError: Boolean
        get() {
            return errorState
        }

    override val isEnd: Boolean
        get() {
            return isEND(currentA!!)
        }

    private fun thestep(var1: Int): IntArray? {
        return if (errorState) {
            currentA
        } else if (choices == null) {
            output.outln("DEADLOCK")
            errorState = true
            currentA
        } else {
            val var2: Iterator<*> = choices!!.iterator()
            var var3: IntArray
            do {
                if (!var2.hasNext()) {
                    return currentA
                }
                var3 = var2.next() as IntArray
            } while (var3[Nmach] != var1)
            var3 = nonDetSelect(var3)
            errorState = coder.encode(var3) == null
            if (errorState) {
                var3
            } else {
                var3.also { currentA = it }
            }
        }
    }

    private fun step(var1: Int): IntArray? {
        val var2 = thestep(var1)
        eman?.post(LTSEvent(0, var2, actionName[var1]))
        return var2
    }

    fun nonDetSelect(var1: IntArray): IntArray {
        val var2 = choices!!.indexOf(var1)
        var var3 = var2 + 1
        while (var3 < choices!!.size && var1[Nmach] == choices!![var3][Nmach]) {
            ++var3
        }
        return if (var2 + 1 == var3) {
            var1
        } else {
            val var4 = var2 + abs(rand.nextInt()) % (var3 - var2)
            choices!![var4]
        }
    }

    override val menuNames: Array<String>
        get() {
            return menuAlpha!!
        }

    override val allNames: Array<String>
        get() {
            return actionName
        }

    override val priority: Boolean
        get() {
            return lowpriority
        }

    override val priorityActions: BitSet?
        get() {
            return if (priorLabels == null) {
                null
            } else {
                val var1 = BitSet()
                for (var2 in 1 until actionName.size) {
                    val var3 = actionToIndex!!.get(var2)
                    if (var3 != null && (lowpriority && !highAction!![var2] || !lowpriority && highAction!![var2])) {
                        var1.set(var3)
                    }
                }
                var1
            }
        }

    override fun message(var1: String) {
        output.outln(var1)
    }

    companion object {

        private const val SUCCESS = 0

        private const val DEADLOCK = 1

        private const val ERROR = 2

        private const val FOUND = 3

        var partialOrderReduction = false

        var preserveObsEquiv = true

    }

}