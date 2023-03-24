package io.github.vinccool96.ltsa.ltsatool.lts

import java.util.*

class RunMenu private constructor(var name: String, var params: String?, var actions: Relation?,
        var controls: Relation?, var alphabet: Vector<String>?) {

    constructor(name: String, params: String?, actions: Relation?, controls: Relation?) : this(name, params, actions,
            controls, null)

    constructor(name: String, alphabet: Vector<String>?) : this(name, null, null, null, alphabet)

    val isCustom: Boolean
        get() {
            return params != null
        }

    companion object {

        var menus: Hashtable<String, RunMenu>? = null

        fun init() {
            menus = Hashtable()
        }

        fun add(var0: RunMenu) {
            menus!![var0.name] = var0
        }

    }

}