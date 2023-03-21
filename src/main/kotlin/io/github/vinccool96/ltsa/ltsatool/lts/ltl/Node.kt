package io.github.vinccool96.ltsa.ltsatool.lts.ltl

import io.github.vinccool96.ltsa.ltsatool.lts.LTSOutput
import java.util.*

class Node(var1: SortedSet<Node>?, var2: SortedSet<Formula>?, var3: SortedSet<Formula>?, var4: SortedSet<Formula>?,
        var5: BitSet?, var6: BitSet?) : Comparable<Node> {

    var id = aut!!.newId()

    var equivId = -1

    var incoming: SortedSet<Node> = if (var1 != null) TreeSet(var1) else TreeSet()

    var oldf: SortedSet<Formula> = if (var2 != null) TreeSet(var2) else TreeSet()

    var newf: SortedSet<Formula> = if (var3 != null) TreeSet(var3) else TreeSet()

    var next: SortedSet<Formula> = if (var4 != null) TreeSet(var4) else TreeSet()

    var accepting = BitSet()

    var rightOfU = BitSet()

    private var otherSource: Node? = null

    constructor() : this(null, null, null, null, null, null)

    constructor(var1: Formula) : this() {
        collapsed = false
        if (var1 !is True) {
            this.decomposeAndforNext(var1)
        }
    }

    init {
        if (var5 != null) {
            accepting.or(var5)
        }
        if (var6 != null) {
            rightOfU.or(var6)
        }
    }

    override operator fun compareTo(other: Node): Int {
        return id - other.id
    }

    fun decomposeAndforNext(var1: Formula) {
        if (var1 is And) {
            decomposeAndforNext(var1.left)
            decomposeAndforNext(var1.right)
        } else if (!isRedundant(next, null, var1)) {
            next.add(var1)
        }
    }

    private fun isRedundant(var1: SortedSet<Formula>, var2: SortedSet<Formula>?, var3: Formula): Boolean {
        return factory!!.specialCaseV(var3, var1) || factory!!.syntaxImplied(var3, var1,
                var2) && (var3 !is Until || factory!!.syntaxImplied(var3.getSub2(), var1, var2))
    }

    private fun split(var1: Formula): Node {
        val var2 = Node(incoming, oldf, newf, next, accepting, rightOfU)
        var var3 = var1.getSub2()
        if (!oldf.contains(var3)) {
            var2.newf.add(var3)
        }
        if (var1 is Release) {
            var3 = var1.getSub1()
            if (!oldf.contains(var3)) {
                var2.newf.add(var3)
            }
        }
        var3 = var1.getSub1()
        if (!oldf.contains(var3)) {
            newf.add(var3)
        }
        var3 = if (var1 !is Until && var1 !is Release) null else var1
        if (var3 != null) {
            decomposeAndforNext(var3)
        }
        if (var1 is Until) {
            accepting.set(var1.getUI())
            var2.accepting.set(var1.getUI())
        }
        if (var1.isRightOfUntil) {
            rightOfU.or(var1.getRofWU()!!)
            var2.rightOfU.or(var1.getRofWU()!!)
        }
        if (var1.isLiteral) {
            oldf.add(var1)
            var2.oldf.add(var1)
        }
        return var2
    }

    fun expand(var1: MutableList<Node>): MutableList<Node> {
        return if (newf.isEmpty()) {
            if (id != 0) {
                accepting.andNot(rightOfU)
            }
            val var5 = alreadyThere(var1)
            if (var5 != null) {
                var5.modify(this)
                var1
            } else {
                val var6 = Node()
                var6.incoming.add(this)
                var6.newf.addAll(next)
                var1.add(this)
                var6.expand(var1)
            }
        } else {
            val var2 = newf.first() as Formula
            newf.remove(var2)
            if (contradiction(var2)) {
                var1
            } else {
                val var3 = TreeSet<Formula>()
                var3.addAll(oldf)
                var3.addAll(newf)
                if (isRedundant(var3, next, var2)) {
                    expand(var1)
                } else {
                    if (!var2.isLiteral) {
                        if (var2 is Or || var2 is Until || var2 is Release) {
                            val var7 = this.split(var2)
                            return var7.expand(expand(var1))
                        }
                        if (var2 is And) {
                            var var4 = var2.getSub1()
                            if (!oldf.contains(var4)) {
                                newf.add(var4)
                            }
                            var4 = var2.getSub2()
                            if (!oldf.contains(var4)) {
                                newf.add(var4)
                            }
                            if (var2.isRightOfUntil) {
                                rightOfU.or(var2.getRofWU()!!)
                            }
                            return expand(var1)
                        }
                        if (var2 is Next) {
                            decomposeAndforNext(var2.getSub1()!!)
                            if (var2.isRightOfUntil) {
                                rightOfU.or(var2.getRofWU()!!)
                            }
                            return expand(var1)
                        }
                    }
                    if (var2 !is True) {
                        oldf.add(var2)
                    }
                    if (var2.isRightOfUntil) {
                        rightOfU.or(var2.getRofWU()!!)
                    }
                    expand(var1)
                }
            }
        }
    }

    private fun contradiction(var1: Formula): Boolean {
        return factory!!.syntaxImplied(factory!!.makeNot(var1), oldf, next)
    }

    private fun alreadyThere(var1: List<*>): Node? {
        val var2 = var1.iterator()
        var var3: Node
        do {
            if (!var2.hasNext()) {
                return null
            }
            var3 = var2.next() as Node
        } while (next != var3.next || !compareAccepting(var3))
        return var3
    }

    private fun compareAccepting(var1: Node): Boolean {
        return if (id == 0 && !collapsed) true else accepting == var1.accepting
    }

    fun printNode(var1: LTSOutput) {
        var1.outln("NODE $id equivId $equivId")
        printIdSet(var1, "INCOMING", incoming)
        printFormulaSet(var1, "NEW", newf)
        var1.outln(".")
        printFormulaSet(var1, "OLD", oldf)
        var1.outln(".")
        printFormulaSet(var1, "NEXT", next)
        var1.outln(".")
        var1.outln("ACCEPTING:- $accepting")
        var1.outln("RIGHTOFU:- $rightOfU")
        if (otherSource != null) {
            var1.outln("OTHERSOURCE " + otherSource!!.id + " ************** ")
            var var2 = otherSource
            while (var2 != null) {
                var2.printNode(var1)
                var2 = var2.otherSource
                if (var2 === this) {
                    break
                }
            }
        }
    }

    private fun modify(var1: Node) {
        var var2 = false
        var var3 = this
        var var4: Node? = this
        if (id == 0 && !collapsed) {
            accepting = var1.accepting
            collapsed = true
        }
        while (var4 != null) {
            if (var4.oldf == var1.oldf) {
                var4.incoming.addAll(var1.incoming)
                var2 = true
            }
            var3 = var4
            var4 = var4.otherSource
        }
        if (!var2) {
            var3.otherSource = var1
        }
    }

    private val isSafetyAcc: Boolean
        get() {
            return if (next.isEmpty()) {
                true
            } else {
                val var1: Iterator<*> = next.iterator()
                var var2: Formula?
                do {
                    if (!var1.hasNext()) {
                        return true
                    }
                    var2 = var1.next() as Formula?
                } while (var2 is Release)
                false
            }
        }

    fun makeTransitions(var1: Array<State?>) {
        if (var1[id] == null) {
            var1[id] = State(equivId)
        } else {
            var1[id]!!.stateId = equivId
        }
        val var3 = isSafetyAcc
        var var5: Int
        var var4: Node? = this
        while (var4 != null) {
            val var6 = var4.incoming.iterator()
            while (var6.hasNext()) {
                val var7 = var6.next()
                var5 = var7.id
                if (var1[var5] == null) {
                    var1[var5] = State()
                }
                var1[var5]!!.add(Transition(var4.oldf, equivId, accepting, var3))
            }
            var4 = var4.otherSource
        }
    }

    companion object {

        var factory: FormulaFactory? = null

        var aut: GeneralizedBuchiAutomata? = null

        private var collapsed = false

        fun printFormulaSet(var0: LTSOutput, var1: String, var2: SortedSet<out Formula>) {
            var0.out("$var1:- ")
            val var3 = var2.iterator()
            while (var3.hasNext()) {
                val var4 = var3.next()
                var0.out("$var4, ")
            }
        }

        fun printIdSet(var0: LTSOutput, var1: String, var2: SortedSet<Node>) {
            var0.out("$var1:- ")
            val var3 = var2.iterator()
            while (var3.hasNext()) {
                val var4 = var3.next()
                var0.out("${var4.id}, ")
            }
            var0.outln(".")
        }

    }

}
