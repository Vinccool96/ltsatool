package io.github.vinccool96.ltsa.ltsatool.custom

import io.github.vinccool96.ltsa.ltsatool.lts.Relation
import io.github.vinccool96.ltsa.ltsatool.utils.toArrayOfNotNull
import java.util.*

class ControlActionRegistry(var actionsToControls: Relation, var msg: AnimationMessage) {

    var actionNumber = Hashtable<String, Int>()

    var controlNumber = Hashtable<String, Int>()

    var controlsToActions: Relation? = null

    var controlMap: Array<IntArray?> = arrayOf()

    var actionMap: Array<IntArray> = arrayOf()

    var actionAlphabet: Array<String> = arrayOf()

    var controlAlphabet: Array<String> = arrayOf()

    var controlState: BooleanArray = booleanArrayOf()

    fun getAnimatorControls() {
        var var1 = 0
        val var2 = Vector<String>()
        val var3 = controlsToActions!!.keys()
        while (var3.hasMoreElements()) {
            val var4 = var3.nextElement() as String
            controlNumber[var4] = var1
            var2.addElement(var4)
            ++var1
        }
        var2.copyInto(controlAlphabet)
    }

    val controls: Vector<String>
        get() {
            val var1 = Vector<String>()
            val var2 = actionsToControls.keys()
            while (var2.hasMoreElements()) {
                val var3 = var2.nextElement()
                var1.addElement(var3 as String)
            }
            return var1
        }

    fun controlled(var1: String): Int {
        val var2 = actionNumber[var1]
        return var2 ?: -1
    }

    fun initMap(var1: Array<String>) {
        actionAlphabet = var1
        for (var3 in 1 until var1.size) {
            actionNumber[var1[var3]] = var3
        }
        var var6 = actionsToControls.keys()
        val var4 = Vector<Any>()
        while (var6.hasMoreElements()) {
            val var5 = var6.nextElement()
            if (actionNumber[var5] == null) {
                var4.addElement(var5)
            }
        }
        var6 = var4.elements()
        while (var6.hasMoreElements()) {
            actionsToControls.remove(var6.nextElement())
        }
        controlsToActions = actionsToControls.inverse()
        getAnimatorControls()
        controlMap = arrayOfNulls(controlAlphabet.size)
        controlState = BooleanArray(controlAlphabet.size)
        for (var7 in controlState.indices) {
            controlState[var7] = true
        }
        initControlMap()
        initActionMap()
    }

    protected fun initControlMap() {
        val var2 = controlsToActions!!.keys()
        while (true) {
            while (var2.hasMoreElements()) {
                val var3 = var2.nextElement() as String
                val var4 = controlNumber[var3] as Int
                val var5 = controlsToActions!![var3]
                var var1: IntArray
                if (var5 is String) {
                    val var11 = actionNumber[var5] as Int
                    var1 = intArrayOf(var11)
                    controlMap[var4] = var1
                } else {
                    val var6 = var5 as Vector<String>
                    var1 = IntArray(var6.size)
                    val var7 = var6.elements()
                    var var8 = 0
                    while (var7.hasMoreElements()) {
                        val var9 = var7.nextElement() as String
                        val var10 = actionNumber[var9] as Int
                        var1[var8] = var10
                        ++var8
                    }
                    controlMap[var4] = var1
                }
            }
            return
        }
    }

    protected fun initActionMap() {
        val actionMap = arrayOfNulls<IntArray>(actionAlphabet.size)
        val var2 = actionsToControls.keys()
        while (true) {
            while (var2.hasMoreElements()) {
                val var3 = var2.nextElement() as String
                val var4 = actionNumber[var3]!!
                val var5 = actionsToControls[var3]
                var var1: IntArray
                if (var5 is String) {
                    val var11 = controlNumber[var5]!!
                    var1 = intArrayOf(var11)
                    actionMap[var4] = var1
                } else {
                    val var6 = var5 as Vector<String>
                    var1 = IntArray(var6.size)
                    val var7 = var6.elements()
                    var var8 = 0
                    while (var7.hasMoreElements()) {
                        val var9 = var7.nextElement()
                        val var10 = controlNumber[var9]!!
                        var1[var8] = var10
                        ++var8
                    }
                    actionMap[var4] = var1
                }
            }
            break
        }
        this.actionMap = actionMap.toArrayOfNotNull()
    }

    fun print() {
        for (var1 in controlMap.indices) {
            println(controlAlphabet[var1])
            for (var2 in 0 until controlMap[var1]!!.size) {
                print(" " + actionAlphabet[controlMap[var1]!![var2]])
            }
            println()
        }
    }

    fun mapControl(var1: String, var2: BooleanArray, var3: Boolean) {
        val var4 = if (var3) {
            "-enable-"
        } else {
            "-disabl-"
        }
        msg.debugMsg("-control$var4$var1")
        val var5 = controlNumber[var1]
        if (var5 != null) {
            controlState[var5] = var3
            if (controlMap[var5] != null) {
                for (var7 in 0 until controlMap[var5]!!.size) {
                    val var8 = controlMap[var5]!![var7]
                    if (actionMap[var8].size == 1) {
                        var2[var8] = var3
                    } else {
                        var var9 = var3
                        for (var10 in 0 until actionMap[var8].size) {
                            var9 = var9 && controlState[actionMap[var8][var10]]
                        }
                        var2[var8] = var9
                    }
                }
            }
        }
    }

}