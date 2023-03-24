package io.github.vinccool96.ltsa.ltsatool.lts

import io.github.vinccool96.ltsa.ltsatool.lts.Expression.evaluate
import java.util.*


class CompositeBody {

    var singleton: ProcessRef? = null

    var procRefs: Vector<CompositeBody>? = null

    var boolexpr: Stack<Symbol>? = null

    var thenpart: CompositeBody? = null

    var elsepart: CompositeBody? = null

    var range: ActionLabels? = null

    var prefix: ActionLabels? = null

    var accessSet: ActionLabels? = null

    var relabelDefns: Vector<RelabelDefn>? = null

    fun compose(var1: CompositionExpression, var2: Vector<Any>, var3: Hashtable<String, Value>) {
        val var4 = if (accessSet == null) null else accessSet!!.getActions(var3, var1.constants)
        val var5 = RelabelDefn.getRelabels(relabelDefns, var1.constants!!, var3)
        if (boolexpr != null) {
            if (evaluate(boolexpr!!, var3, var1.constants) != 0) {
                thenpart!!.compose(var1, var2, var3)
            } else if (elsepart != null) {
                elsepart!!.compose(var1, var2, var3)
            }
        } else if (range != null) {
            range!!.initContext(var3, var1.constants)
            while (range!!.hasMoreNames()) {
                range!!.nextName()
                thenpart!!.compose(var1, var2, var3)
            }
            range!!.clearContext()
        } else {
            val var6 = getPrefixedMachines(var1, var3)
            var var7 = var6.elements()
            var var8: Any
            var var9: CompactState
            var var12: CompositeState
            if (var4 != null) {
                while (var7.hasMoreElements()) {
                    var8 = var7.nextElement()
                    if (var8 is CompactState) {
                        var9 = var8
                        var9.addAccess(var4)
                    } else {
                        var12 = var8 as CompositeState
                        var12.addAccess(var4)
                    }
                }
            }
            if (var5 != null) {
                for (var11 in 0 until var6.size) {
                    var8 = var6.elementAt(var11)
                    if (var8 is CompactState) {
                        var9 = var8
                        var9.relabel(var5)
                    } else {
                        var12 = var8 as CompositeState
                        val var10 = var12.relabel(var5, var1.output!!)
                        if (var10 != null) {
                            var6.setElementAt(var10, var11)
                        }
                    }
                }
            }
            var7 = var6.elements()
            while (var7.hasMoreElements()) {
                var2.addElement(var7.nextElement())
            }
        }
    }

    private fun getPrefixedMachines(var1: CompositionExpression, var2: Hashtable<String, Value>): Vector<Any> {
        return if (prefix == null) {
            getMachines(var1, var2)
        } else {
            val var3 = Vector<Any>()
            prefix!!.initContext(var2, var1.constants)
            while (prefix!!.hasMoreNames()) {
                val var4 = prefix!!.nextName()
                val var5 = getMachines(var1, var2)
                val var6 = var5.elements()
                while (var6.hasMoreElements()) {
                    val var7 = var6.nextElement()
                    if (var7 is CompactState) {
                        var7.prefixLabels(var4)
                        var3.addElement(var7)
                    } else {
                        val var9 = var7 as CompositeState
                        var9.prefixLabels(var4)
                        var3.addElement(var9)
                    }
                }
            }
            prefix!!.clearContext()
            var3
        }
    }

    private fun getMachines(var1: CompositionExpression, var2: Hashtable<String, Value>): Vector<Any> {
        val var3 = Vector<Any>()
        if (singleton != null) {
            singleton!!.instantiate(var1, var3, var1.output!!, var2)
        } else if (procRefs != null) {
            val var4 = procRefs!!.elements()
            while (var4.hasMoreElements()) {
                val var5 = var4.nextElement()
                var5.compose(var1, var3, var2)
            }
        }
        return var3
    }

}