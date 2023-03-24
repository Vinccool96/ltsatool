package io.github.vinccool96.ltsa.ltsatool.lts

import io.github.vinccool96.ltsa.ltsatool.lts.Diagnostics.fatal
import io.github.vinccool96.ltsa.ltsatool.lts.Expression.getValue
import java.util.*

class SeqProcessRef(var name: Symbol, var actualParams: Vector<Stack<Symbol>>?) {

    fun instantiate(var1: Hashtable<String, Value>, var2: Hashtable<String, Value>): CompactState? {
        val var3 = paramValues(var1, var2)
        val var4 = if (var3 == null) name.toString() else name.toString() + StateMachine.paramString(var3)
        var var5 = LTSCompiler.compiled!![var4]
        if (var5 == null) {
            var var6 = LTSCompiler.processes!![name.toString()]
            if (var6 != null) {
                var6 = var6.myclone()
                if (actualParams != null && actualParams!!.size != var6.parameters.size) {
                    fatal("actuals do not match formal parameters", name as Symbol?)
                }
                val var7 = StateMachine(var6, var3)
                var5 = var7.makeCompactState()
                output!!.outln("-- compiled:" + var5.name)
            }
        }
        if (var5 == null) {
            val var8 = LTSCompiler.composites!![name.toString()]
            if (var8 != null) {
                val var9: CompositeState = var8.compose(var3)
                var5 = var9.create(output!!)
            }
        }
        return if (var5 != null) {
            LTSCompiler.compiled!![var5.name] = var5
            if (!var5.isSequential()) {
                fatal("process is not sequential - $name", name as Symbol?)
            }
            var5.myclone()
        } else {
            fatal("process definition not found- $name", name as Symbol?)
            null
        }
    }

    private fun paramValues(var1: Hashtable<String, Value>, var2: Hashtable<String, Value>): Vector<Value>? {
        return if (actualParams == null) {
            null
        } else {
            val var3 = actualParams!!.elements()
            val var4 = Vector<Value>()
            while (var3.hasMoreElements()) {
                val var5 = var3.nextElement()
                var4.addElement(getValue(var5, var1, var2))
            }
            var4
        }
    }

    companion object {

        var output: LTSOutput? = null

    }

}