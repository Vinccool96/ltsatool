package io.github.vinccool96.ltsa.ltsatool.lts

import java.util.*

class ChoiceElement : Declaration() {

    var guard: Stack<Symbol>? = null

    var action: ActionLabels? = null

    var stateExpr: StateExpr? = null

    private fun add(var1: Int, var2: Hashtable<String, Value>, var3: StateMachine, var4: ActionLabels) {
        var4.initContext(var2, var3.constants)
        var var6: Symbol?
        while (var4.hasMoreNames()) {
            val var5 = var4.nextName()
            var6 = Symbol(124, var5)
            if (!var3.alphabet.containsKey(var5)) {
                var3.alphabet[var5] = var3.eventLabel.label
            }
            stateExpr!!.endTransition(var1, var6, var2, var3)
        }
        var4.clearContext()
    }

    private fun add(var1: Int, var2: Hashtable<String, Value>, var3: StateMachine, var4: String) {
        val var5 = Symbol(124, var4)
        if (!var3.alphabet.containsKey(var4)) {
            var3.alphabet[var4] = var3.eventLabel.label
        }
        stateExpr!!.endTransition(var1, var5, var2, var3)
    }

    fun addTransition(var1: Int, var2: Hashtable<String, Value>, var3: StateMachine) {
        if ((guard == null || Expression.evaluate(guard!!, var2, var3.constants) != 0) && action != null) {
            this.add(var1, var2, var3, action!!)
        }
    }

    fun myclone(): ChoiceElement {
        val var1 = ChoiceElement()
        var1.guard = guard
        if (action != null) {
            var1.action = action!!.myclone()
        }
        if (stateExpr != null) {
            var1.stateExpr = stateExpr!!.myclone()
        }
        return var1
    }

}