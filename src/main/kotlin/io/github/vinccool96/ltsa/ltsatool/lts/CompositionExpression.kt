package io.github.vinccool96.ltsa.ltsatool.lts

import java.util.*

class CompositionExpression {

    var name: Symbol? = null

    var body: CompositeBody? = null

    var constants: Hashtable<String, Value>? = null

    var init_constants = Hashtable<String, Value>()

    var parameters = Vector<String>()

    var processes: Hashtable<String, ProcessSpec>? = null

    var compiledProcesses: Hashtable<String, CompactState>? = null

    var composites: Hashtable<String, CompositionExpression>? = null

    var output: LTSOutput? = null

    var priorityIsLow = true

    var priorityActions: LabelSet? = null

    var alphaHidden: LabelSet? = null

    var exposeNotHide = false

    var makeDeterministic = false

    var makeMinimal = false

    var makeProperty = false

    var makeCompose = false

    fun compose(var1: Vector<Value>?): CompositeState {
        val var2 = Vector<Any>()
        val var3 = Hashtable<String, Value>()
        constants = init_constants.clone() as Hashtable<String, Value>
        if (var1 != null) {
            doParams(var1)
        }
        body!!.compose(this, var2, var3)
        val var5 = Vector<CompactState>()
        val var6 = var2.elements()
        while (true) {
            while (var6.hasMoreElements()) {
                val var7 = var6.nextElement()
                if (var7 is CompactState) {
                    var5.addElement(var7)
                } else {
                    val var8 = var7 as CompositeState
                    val var9 = var8.machines.elements()
                    while (var9.hasMoreElements()) {
                        var5.addElement(var9.nextElement())
                    }
                }
            }
            val var10 = if (var1 == null) name.toString() else name.toString() + StateMachine.paramString(var1)
            val var11 = CompositeState(var10, var5)
            var11.priorityIsLow = priorityIsLow
            var11.priorityLabels = computeAlphabet(priorityActions)
            var11.hidden = computeAlphabet(alphaHidden)
            var11.exposeNotHide = exposeNotHide
            var11.makeDeterministic = makeDeterministic
            var11.makeMinimal = makeMinimal
            var11.makeCompose = makeCompose
            if (makeProperty) {
                var11.makeDeterministic = true
                var11.isProperty = true
            }
            return var11
        }
    }

    private fun doParams(var1: Vector<Value>) {
        val var2 = var1.elements()
        val var3 = parameters.elements()
        while (var2.hasMoreElements() && var3.hasMoreElements()) {
            constants!![var3.nextElement()] = var2.nextElement()
        }
    }

    private fun computeAlphabet(var1: LabelSet?): Vector<String>? {
        return var1?.getActions(constants)
    }

}