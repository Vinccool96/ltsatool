package io.github.vinccool96.ltsa.ltsatool.lts

import io.github.vinccool96.ltsa.ltsatool.lts.Diagnostics.fatal
import io.github.vinccool96.ltsa.ltsatool.lts.Diagnostics.warning
import io.github.vinccool96.ltsa.ltsatool.lts.Expression.evaluate
import java.util.*


class StateDefn : Declaration() {

    var name: Symbol? = null

    var accept = false

    var range: ActionLabels? = null

    var stateExpr: StateExpr? = null

    private fun checkPut(var1: String, var2: StateMachine) {
        if (var2.explicit_states.containsKey(var1)) {
            fatal("duplicate definition -$name", name)
        } else {
            var2.explicit_states[var1] = var2.stateLabel.label
        }
    }

    override fun explicitStates(stateMachine: StateMachine) {
        if (range == null) {
            val var2 = name.toString()
            if (var2 == "STOP" || var2 == "ERROR" || var2 == "END") {
                fatal("reserved local process name -$name", name)
            }
            checkPut(var2, stateMachine)
        } else {
            val var3 = Hashtable<String, Value>()
            range!!.initContext(var3, stateMachine.constants)
            while (range!!.hasMoreNames()) {
                checkPut(name.toString() + "." + range!!.nextName(), stateMachine)
            }
            range!!.clearContext()
        }
    }

    private fun crunchAlias(var1: StateExpr?, var2: String, var3: Hashtable<String, Value>, var4: StateMachine) {
        val var5 = var1!!.evalName(var3, var4)
        var var6 = var4.explicit_states[var5]
        if (var6 == null) {
            if (var5 == "STOP") {
                var4.explicit_states["STOP"] = var4.stateLabel.label.also { var6 = it }
            } else if (var5 == "ERROR") {
                var4.explicit_states["ERROR"] = (-1).also { var6 = it }
            } else if (var5 == "END") {
                var4.explicit_states["END"] = var4.stateLabel.label.also { var6 = it }
            } else {
                var4.explicit_states["ERROR"] = (-1).also { var6 = it }
                warning("$var5 defined to be ERROR", "definition not found- $var5", var1.name)
            }
        }
        var var7: CompactState? = null
        if (var1.processes != null) {
            var7 = var1.makeInserts(var3, var4)
        }
        if (var7 != null) {
            var4.preAddSequential(var4.explicit_states[var2]!!, var6!!, var7)
        } else {
            var4.aliases[var4.explicit_states[var2]] = var6
        }
    }

    override fun crunch(stateMachine: StateMachine) {
        if (stateExpr!!.name != null || stateExpr!!.boolexpr != null) {
            val var2 = Hashtable<String, Value>()
            if (range == null) {
                crunchit(stateMachine, var2, stateExpr!!, name.toString())
            } else {
                range!!.initContext(var2, stateMachine.constants)
                while (range!!.hasMoreNames()) {
                    val var3 = "" + name + "." + range!!.nextName()
                    crunchit(stateMachine, var2, stateExpr!!, var3)
                }
                range!!.clearContext()
            }
        }
    }

    private fun crunchit(var1: StateMachine, var2: Hashtable<String, Value>, var3: StateExpr, var4: String) {
        var var5: StateExpr? = var3
        if (var5!!.name != null) {
            crunchAlias(var5, var4, var2, var1)
        } else if (var5.boolexpr != null) {
            var5 = if (evaluate(var5.boolexpr!!, var2, var1.constants) != 0) {
                var5.thenpart
            } else {
                var5.elsepart
            }
            if (var5 != null) {
                crunchit(var1, var2, var5, var4)
            }
        }
    }

    override fun transition(stateMachine: StateMachine) {
        if (stateExpr!!.name == null) {
            val var3 = Hashtable<String, Value>()
            var var2: Int
            if (range == null) {
                var2 = stateMachine.explicit_states["" + name]!!
                stateExpr!!.firstTransition(var2, var3, stateMachine)
                if (accept) {
                    if (!stateMachine.alphabet.containsKey("@")) {
                        stateMachine.alphabet["@"] = stateMachine.eventLabel.label
                    }
                    val var4 = Symbol(124, "@")
                    stateMachine.transitions.addElement(Transition(var2, var4, var2))
                }
            } else {
                range!!.initContext(var3, stateMachine.constants)
                while (range!!.hasMoreNames()) {
                    var2 = stateMachine.explicit_states["" + name + "." + range!!.nextName()]!!
                    stateExpr!!.firstTransition(var2, var3, stateMachine)
                }
                range!!.clearContext()
            }
        }
    }

    fun myclone(): StateDefn {
        val var1 = StateDefn()
        var1.name = name
        var1.accept = accept
        if (range != null) {
            var1.range = range!!.myclone()
        }
        if (stateExpr != null) {
            var1.stateExpr = stateExpr!!.myclone()
        }
        return var1
    }

}