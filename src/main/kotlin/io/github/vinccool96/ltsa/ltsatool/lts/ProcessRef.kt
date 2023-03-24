package io.github.vinccool96.ltsa.ltsatool.lts

import io.github.vinccool96.ltsa.ltsatool.lts.Diagnostics.fatal
import io.github.vinccool96.ltsa.ltsatool.lts.Expression.getValue
import io.github.vinccool96.ltsa.ltsatool.lts.ltl.AssertDefinition
import java.util.*


class ProcessRef {

    var name: Symbol? = null

    var actualParams: Vector<Stack<Symbol>>? = null

    fun instantiate(var1: CompositionExpression, var2: Vector<Any>, var3: LTSOutput, var4: Hashtable<String, Value>) {
        val var5 = paramValues(var4, var1)
        val var6 = if (var5 == null) name.toString() else name.toString() + StateMachine.paramString(var5)
        var var7 = var1.compiledProcesses!![var6]
        if (var7 != null) {
            var2.addElement(var7.myclone())
        } else {
            val var8 = var1.processes!![name.toString()]
            if (var8 != null) {
                if (actualParams != null && actualParams!!.size != var8.parameters.size) {
                    fatal("actuals do not match formal parameters", name)
                }
                val var13: Any
                var13 = if (!var8.imported()) {
                    val var14 = StateMachine(var8, var5)
                    var14.makeCompactState()
                } else {
                    AutCompactState(var8.name!!, var8.importFile!!)
                }
                var2.addElement(var13.myclone())
                var1.compiledProcesses!![var13.name] = var13
                if (!var8.imported()) {
                    var1.output!!.outln("Compiled: " + var13.name)
                } else {
                    var1.output!!.outln("Imported: " + var13.name)
                }
            } else {
                var7 = AssertDefinition.compileConstraint(var3, name!!, var6, var5)
                if (var7 != null) {
                    var2.addElement(var7.myclone())
                    var1.compiledProcesses!![var7.name] = var7
                } else {
                    val var9 = var1.composites!!.get(name.toString())
                    if (var9 == null) {
                        fatal("definition not found- $name", name)
                        return
                    }
                    if (actualParams != null && actualParams!!.size != var9.parameters.size) {
                        fatal("actuals do not match formal parameters", name)
                        return
                    }
                    val var10: CompositeState
                    if (var9 === var1) {
                        val var11 = var1.constants!!.clone() as Hashtable<String, Value>
                        var10 = var9.compose(var5)
                        var1.constants = var11
                    } else {
                        var10 = var9.compose(var5)
                    }
                    if (var10.needNotCreate()) {
                        val var12 = var10.machines.elements()
                        while (var12.hasMoreElements()) {
                            var7 = var12.nextElement()
                            var7!!.name = var10.name + "." + var7.name
                        }
                        var2.addElement(var10)
                    } else {
                        var7 = var10.create(var3)
                        var1.compiledProcesses!![var7!!.name] = var7
                        var1.output!!.outln("Compiled: " + var7.name)
                        var2.addElement(var7.myclone())
                    }
                }
            }
        }
    }

    private fun paramValues(var1: Hashtable<String, Value>, var2: CompositionExpression): Vector<Value>? {
        return if (actualParams == null) {
            null
        } else {
            val var3 = actualParams!!.elements()
            val var4 = Vector<Value>()
            while (var3.hasMoreElements()) {
                val var5 = var3.nextElement()
                var4.addElement(getValue(var5, var1, var2.constants!!))
            }
            var4
        }
    }

}