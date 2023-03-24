package io.github.vinccool96.ltsa.ltsatool.lts

import io.github.vinccool96.ltsa.ltsatool.lts.Diagnostics.fatal
import java.io.File
import java.util.*


class ProcessSpec : Declaration() {

    var name: Symbol? = null

    var constants: Hashtable<String, Value>? = null

    var init_constants = Hashtable<String, Value>()

    var parameters = Vector<String>()

    var stateDefns = Vector<StateDefn>()

    var alphaAdditions: LabelSet? = null

    var alphaHidden: LabelSet? = null

    var alphaRelabel: Vector<RelabelDefn>? = null

    var isProperty = false

    var isMinimal = false

    var isDeterministic = false

    var exposeNotHide = false

    var importFile: File? = null

    fun imported(): Boolean {
        return importFile != null
    }

    fun getname(): String {
        constants = init_constants.clone() as Hashtable<String, Value>
        val var1 = stateDefns.firstElement()
        name = var1.name
        if (var1.range != null) {
            fatal("process name cannot be indexed", name)
        }
        return var1.name.toString()
    }

    override fun explicitStates(stateMachine: StateMachine) {
        val var2 = stateDefns.elements()
        while (var2.hasMoreElements()) {
            val var3 = var2.nextElement()
            var3.explicitStates(stateMachine)
        }
    }

    fun addAlphabet(var1: StateMachine) {
        if (alphaAdditions != null) {
            val var2 = alphaAdditions!!.getActions(constants)!!
            val var3 = var2.elements()
            while (var3.hasMoreElements()) {
                val var4 = var3.nextElement() as String
                if (!var1.alphabet.containsKey(var4)) {
                    var1.alphabet[var4] = var1.eventLabel.label
                }
            }
        }
    }

    fun hideAlphabet(var1: StateMachine) {
        if (alphaHidden != null) {
            var1.hidden = alphaHidden!!.getActions(constants)
        }
    }

    fun relabelAlphabet(var1: StateMachine) {
        if (alphaRelabel != null) {
            var1.relabels = Relation()
            val var2 = alphaRelabel!!.elements()
            while (var2.hasMoreElements()) {
                val var3 = var2.nextElement()
                var3.makeRelabels(constants!!, var1.relabels!!)
            }
        }
    }

    override fun crunch(stateMachine: StateMachine) {
        val var2 = stateDefns.elements()
        while (var2.hasMoreElements()) {
            val var3 = var2.nextElement() as Declaration
            var3.crunch(stateMachine)
        }
    }

    override fun transition(stateMachine: StateMachine) {
        val var2 = stateDefns.elements()
        while (var2.hasMoreElements()) {
            val var3 = var2.nextElement()
            var3.transition(stateMachine)
        }
    }

    fun doParams(var1: Vector<Value>) {
        val var2 = var1.elements()
        val var3 = parameters.elements()
        while (var2.hasMoreElements() && var3.hasMoreElements()) {
            constants!![var3.nextElement()] = var2.nextElement()
        }
    }

    fun myclone(): ProcessSpec {
        val var1 = ProcessSpec()
        var1.name = name
        var1.constants = constants!!.clone() as Hashtable<String, Value>
        var1.init_constants = init_constants
        var1.parameters = parameters
        val var2: Enumeration<*> = stateDefns.elements()
        while (var2.hasMoreElements()) {
            var1.stateDefns.addElement((var2.nextElement() as StateDefn).myclone())
        }
        var1.alphaAdditions = alphaAdditions
        var1.alphaHidden = alphaHidden
        var1.alphaRelabel = alphaRelabel
        var1.isProperty = isProperty
        var1.isMinimal = isMinimal
        var1.isDeterministic = isDeterministic
        var1.exposeNotHide = exposeNotHide
        var1.importFile = importFile
        return var1
    }

}