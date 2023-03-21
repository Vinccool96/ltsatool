package io.github.vinccool96.ltsa.ltsatool.lts

import java.util.*

// TODO: extend Automata
class Analyser(var1: CompositeState, var2: LTSOutput, var3: EventManager?, var4: Boolean) : Animator {

    private var cs: CompositeState? = var1

    private var sm: Array<CompactState>

    private var output: LTSOutput? = var2

    private val alphabet: Hashtable<Short, Short> = Hashtable()

    private var actionMap: Hashtable<Short, Short> = Hashtable()

    private var actionCount: IntArray

    private var actionName: Array<String>

    private var Nmach = 0

    private var Mbase: IntArray

    private val analysed: MyHashStack? = null

    private var stateNo = 0

    private var stateCount = 0

    private var violated: BooleanArray

    private var deadlockDetected = false

    private val SUCCESS = 0

    private val DEADLOCK = 1

    private val ERROR = 2

    private val FOUND = 3

    private var eman: EventManager? = var3

    private var lowpriority = false

    private var priorLabels: Vector<Short>? = null

    private var highAction: BitSet? = null

    private var acceptEvent = 0

    private var asteriskEvent = 0

    private var visible: BitSet? = null

    private var coder: StateCodec? = null

    private var canTerminate = false

    var partialOrderReduction = false

    var preserveObsEquiv = true

    private var partial: PartialOrder? = null

    private val compTrans: MyList? = null

    private var endSequence = 0

    var trace: LinkedList<*>? = null

    var errorMachine = 0

    var savedPartial: PartialOrder? = null

    private val menuAlpha: Array<String>

    private val actionToIndex: Hashtable<Short, Short>?

    private val indexToAction: Hashtable<Short, Short>?

    private val currentA: IntArray

    @Volatile
    private var choices: MutableList<Short>

    private var errorState = false

    private var _replay: Enumeration<*>? = null

    private var _replayAction: String? = null

    var theChoice = 0

    var rand: Random? = null

    constructor(var1: CompositeState, var2: LTSOutput, var3: EventManager?) : this(var1, var2, var3, false)

    init {
        stateNo = 0
        stateCount = 0
        deadlockDetected = false
        lowpriority = true
        priorLabels = null
        highAction = null
        acceptEvent = -1
        asteriskEvent = -1
        canTerminate = false
        partial = null
        endSequence = -99999
        savedPartial = null
        errorState = false
        _replay = null
        _replayAction = null
        theChoice = 0
        rand = Random()
        if (var1.priorityLabels != null) {
            lowpriority = var1.priorityIsLow
            priorLabels = var1.priorityLabels
            highAction = BitSet()
        }
        sm = arrayOfNulls(var1.machines.size)
        violated = BooleanArray(var1.machines.size)
        var var5: Enumeration<*> = var1.machines.elements()
        var var6: Int
        var6 = 0
        while (var5.hasMoreElements()) {
            sm[var6] = (var5.nextElement() as CompactState).myclone()
            ++var6
        }
        Nmach = sm.length
        var2.outln("Composition:")
        var2.out(var1.name + " = ")
        var6 = 0
        while (var6 < sm.length) {
            var2.out(sm[var6].name!!)
            if (var6 < sm.length - 1) {
                var2.out(" || ")
            }
            ++var6
        }
        var2.outln("")
        if (priorLabels != null) {
            if (lowpriority) {
                var2.out("\t>> ")
            } else {
                var2.out("\t<< ")
            }
            var2.outln(Alphabet(var1.priorityLabels).toString())
        }
        Mbase = IntArray(Nmach)
        var2.outln("State Space:")
        var6 = 0
        while (var6 < sm.length) {
            var2.out(" " + sm[var6].maxStates + " ")
            if (var6 < sm.length - 1) {
                var2.out("*")
            }
            Mbase[var6] = sm[var6].maxStates
            ++var6
        }
        coder = StateCodec(Mbase)
        var2.outln("= 2 ** " + coder.bits())
        val var13 = HashSet<Any>()
        val var7 = HashSet<Any>()
        val var8 = Counter(0)
        var var9: Int
        var var10: Int
        var9 = 0
        while (var9 < sm.length) {
            var10 = 0
            while (var10 < sm[var9].alphabet.length) {
                if (sm[var9].endseq > 0) {
                    var13.add(sm[var9].alphabet[var10])
                } else {
                    var7.add(sm[var9].alphabet[var10])
                }
                var var11 = alphabet[sm[var9].alphabet[var10]] as BitSet?
                if (var11 == null) {
                    var11 = BitSet()
                    var11.set(var9)
                    val var12 = sm[var9].alphabet[var10]
                    alphabet[var12] = var11
                    actionMap[var12] = var8.label()
                } else {
                    var11.set(var9)
                }
                ++var10
            }
            ++var9
        }
        canTerminate = var13.containsAll(var7)
        actionName = arrayOfNulls(alphabet.size())
        actionCount = IntArray(alphabet.size())
        var5 = alphabet.keys()
        while (var5.hasMoreElements()) {
            val var14 = var5.nextElement() as String
            val var15 = alphabet[var14] as BitSet?
            val var16 = actionMap[var14] as Int
            actionName[var16] = var14
            actionCount[var16] = this.countSet(var15)
            if (var14[0] == '@') {
                acceptEvent = var16
            } else if (var14 == "*" && !var4) {
                asteriskEvent = var16
            }
            if (highAction != null) {
                if (!lowpriority) {
                    if (CompactState.contains(var14, priorLabels)) {
                        highAction.set(var16)
                    }
                } else if (!CompactState.contains(var14, priorLabels)) {
                    highAction.set(var16)
                }
            }
        }
        if (highAction != null) {
            if (lowpriority) {
                highAction.set(0)
            } else {
                highAction.clear(0)
            }
            if (acceptEvent > 0) {
                highAction.clear(acceptEvent)
            }
        }
        actionCount[0] = 0
        var9 = 0
        while (var9 < sm.length) {
            var10 = 0
            while (var10 < sm[var9].maxStates) {
                var var17 = sm[var9].states[var10]
                while (var17 != null) {
                    var var18 = var17
                    var17.machine = var9
                    var17.event = (actionMap[sm[var9].alphabet[var17.event]] as Int?)!!
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
        visible = BitSet(actionName.length)
        var9 = 1
        while (var9 < actionName.length) {
            if (var1.hidden == null) {
                visible.set(var9)
            } else if (var1.exposeNotHide) {
                if (CompactState.contains(actionName[var9], var1.hidden)) {
                    visible.set(var9)
                }
            } else if (!CompactState.contains(actionName[var9], var1.hidden)) {
                visible.set(var9)
            }
            ++var9
        }
    }

}