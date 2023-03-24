package io.github.vinccool96.ltsa.ltsatool.lts

import io.github.vinccool96.ltsa.ltsatool.lts.Diagnostics.warning
import io.github.vinccool96.ltsa.ltsatool.lts.Expression.evaluate
import io.github.vinccool96.ltsa.ltsatool.lts.Expression.getValue
import java.util.*


class StateExpr : Declaration() {

    var processes: Vector<SeqProcessRef>? = null

    var name: Symbol? = null

    var expr: Vector<Stack<Symbol>>? = null

    var choices: Vector<ChoiceElement>? = null

    var boolexpr: Stack<Symbol>? = null

    var thenpart: StateExpr? = null

    var elsepart: StateExpr? = null

    fun addSeqProcessRef(var1: SeqProcessRef) {
        if (processes == null) {
            processes = Vector()
        }
        processes!!.addElement(var1)
    }

    fun makeInserts(var1: Hashtable<String, Value>, var2: StateMachine): CompactState? {
        val var3 = Vector<CompactState>()
        val var4 = processes!!.elements()
        while (var4.hasMoreElements()) {
            val var5 = var4.nextElement()
            val var6 = var5.instantiate(var1, var2.constants!!)!!
            if (!var6.isEnd()) {
                var3.addElement(var6)
            }
        }
        return if (var3.size > 0) {
            CompactState.sequentialCompose(var3)
        } else {
            null
        }
    }

    fun instantiate(var1: Int, var2: Hashtable<String, Value>, var3: StateMachine): Int {
        return if (processes == null) {
            var1
        } else {
            val var4 = makeInserts(var2, var3)
            if (var4 == null) {
                var1
            } else {
                val var5: Int = var3.stateLabel.interval(var4.maxStates)
                var4.offsetSeq(var5, var1)
                var3.addSequential(var5, var4)
                var5
            }
        }
    }

    fun firstTransition(var1: Int, var2: Hashtable<String, Value>, var3: StateMachine) {
        if (boolexpr != null) {
            if (evaluate(boolexpr!!, var2, var3.constants) != 0) {
                if (thenpart!!.name == null) {
                    thenpart!!.firstTransition(var1, var2, var3)
                }
            } else if (elsepart!!.name == null) {
                elsepart!!.firstTransition(var1, var2, var3)
            }
        } else {
            addTransition(var1, var2, var3)
        }
    }

    fun addTransition(var1: Int, var2: Hashtable<String, Value>, var3: StateMachine) {
        val var4 = choices!!.elements()
        while (var4.hasMoreElements()) {
            val var5 = var4.nextElement()
            var5.addTransition(var1, var2, var3)
        }
    }

    fun endTransition(var1: Int, var2: Symbol?, var3: Hashtable<String, Value>, var4: StateMachine) {
        if (boolexpr != null) {
            if (evaluate(boolexpr!!, var3, var4.constants) != 0) {
                thenpart!!.endTransition(var1, var2, var3, var4)
            } else {
                elsepart!!.endTransition(var1, var2, var3, var4)
            }
        } else {
            var var5: Int?
            if (name != null) {
                var5 = var4.explicit_states[evalName(var3, var4)]
                if (var5 == null) {
                    if (evalName(var3, var4) == "STOP") {
                        var4.explicit_states["STOP"] = var4.stateLabel.label.also { var5 = it }
                    } else if (evalName(var3, var4) == "ERROR") {
                        var4.explicit_states["ERROR"] = (-1).also { var5 = it }
                    } else if (evalName(var3, var4) == "END") {
                        var4.explicit_states["END"] = var4.stateLabel.label.also { var5 = it }
                    } else {
                        var4.explicit_states[evalName(var3, var4)] = (-1).also { var5 = it }
                        warning(evalName(var3, var4) + " defined to be ERROR",
                                "definition not found- " + evalName(var3, var4), name)
                    }
                }
                var5 = instantiate(var5 ?: 0, var3, var4)
                var4.transitions.addElement(Transition(var1, var2, var5!!))
            } else {
                var5 = var4.stateLabel.label
                var4.transitions.addElement(Transition(var1, var2, var5!!))
                addTransition(var5!!, var3, var4)
            }
        }
    }

    fun evalName(var1: Hashtable<String, Value>, var2: StateMachine): String {
        return if (expr == null) {
            name.toString()
        } else {
            val var3 = expr!!.elements()
            var var4: String
            var var5: Stack<Symbol>
            var4 = name.toString()
            while (var3.hasMoreElements()) {
                var5 = var3.nextElement()
                var4 = var4 + "." + getValue(var5, var1, var2.constants!!)
            }
            var4
        }
    }

    fun myclone(): StateExpr {
        val var1 = StateExpr()
        var1.processes = processes
        var1.name = name
        var1.expr = expr
        if (choices != null) {
            var1.choices = Vector()
            val var2 = choices!!.elements()
            while (var2.hasMoreElements()) {
                var1.choices!!.addElement(var2.nextElement().myclone())
            }
        }
        var1.boolexpr = boolexpr
        if (thenpart != null) {
            var1.thenpart = thenpart!!.myclone()
        }
        if (elsepart != null) {
            var1.elsepart = elsepart!!.myclone()
        }
        return var1
    }

}