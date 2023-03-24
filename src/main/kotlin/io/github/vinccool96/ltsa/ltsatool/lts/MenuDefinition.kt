package io.github.vinccool96.ltsa.ltsatool.lts

import java.util.*

class MenuDefinition {

    var name: Symbol? = null

    var actions: ActionLabels? = null

    var params: Symbol? = null

    var target: Symbol? = null

    var actionMapDefn: Vector<RelabelDefn>? = null

    var controlMapDefn: Vector<RelabelDefn>? = null

    var animations: Vector<AnimationPart>? = null

    fun makeRunMenu(): RunMenu {
        val var1 = name.toString()
        return if (params == null) {
            val var4 = actions!!.getActions(null, null)
            RunMenu(var1, var4)
        } else {
            var var2 = RelabelDefn.getRelabels(actionMapDefn)
            var var3 = RelabelDefn.getRelabels(controlMapDefn)
            var2 = var2?.inverse() ?: Relation()
            var3 = var3?.inverse() ?: Relation()
            includeParts(var2, var3)
            RunMenu(var1, if (params == null) null else params.toString(), var2, var3)
        }
    }

    protected fun includeParts(var1: Relation, var2: Relation) {
        if (animations != null) {
            val var3 = animations!!.elements()
            while (var3.hasMoreElements()) {
                val var4 = var3.nextElement()
                var4.makePart()
                var1.union(var4.actions)
                var2.union(var4.controls)
            }
        }
    }

    fun addAnimationPart(var1: Symbol, var2: Vector<RelabelDefn>?) {
        if (animations == null) {
            animations = Vector()
        }
        animations!!.addElement(AnimationPart(var1, var2))
    }

    companion object {

        var definitions: Hashtable<String, MenuDefinition>? = null

        fun compile() {
            RunMenu.init()
            val var0 = definitions!!.elements()
            while (var0.hasMoreElements()) {
                val var1 = var0.nextElement()
                RunMenu.add(var1.makeRunMenu())
            }
        }

        fun names(): Array<String>? {
            return if (definitions == null) {
                null
            } else {
                if (definitions!!.size == 0) {
                    null
                } else {
                    definitions!!.keys().toList().toTypedArray()
                }
            }
        }

        fun enabled(var0: String): BooleanArray? {
            return if (definitions == null) {
                null
            } else {
                val var1 = definitions!!.size
                if (var1 == 0) {
                    null
                } else {
                    val var2 = BooleanArray(var1)
                    val var3 = definitions!!.keys()
                    var var5: MenuDefinition?
                    var var4 = 0
                    while (var3.hasMoreElements()) {
                        var5 = definitions!![var3.nextElement()]
                        var2[var4++] = if (var5!!.target == null) true else var0 == var5.target.toString()
                    }
                    var2
                }
            }
        }

    }

    class AnimationPart(var name: Symbol, var relabels: Vector<RelabelDefn>?) {

        var compiled: RunMenu? = null

        fun makePart() {
            val var1 = definitions!![name.toString()]
            if (var1 == null) {
                Diagnostics.fatal("Animation not found: $name", name as Symbol?)
            } else if (var1.params == null) {
                Diagnostics.fatal("Not an animation: $name", name as Symbol?)
            } else {
                compiled = var1.makeRunMenu()
                if (relabels != null) {
                    val var2 = RelabelDefn.getRelabels(relabels)!!
                    if (compiled!!.actions != null) {
                        compiled!!.actions!!.relabel(var2)
                    }
                    if (compiled!!.controls != null) {
                        compiled!!.controls!!.relabel(var2)
                    }
                }
            }
        }

        val actions: Relation?
            get() {
                return if (compiled != null) compiled!!.actions else null
            }

        val controls: Relation?
            get() = if (compiled != null) compiled!!.controls else null
    }

}