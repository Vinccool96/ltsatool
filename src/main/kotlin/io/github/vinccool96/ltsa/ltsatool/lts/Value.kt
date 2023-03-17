package io.github.vinccool96.ltsa.ltsatool.lts

class Value {

    private val iVal: Int

    private val sVal: String

    private var sOnly: Boolean

    constructor(var1: Int) {
        iVal = var1
        sOnly = false
        sVal = var1.toString()
    }

    constructor(var1: String) {
        var i: Int? = null
        sVal = var1
        try {
            i = var1.toInt()
            sOnly = false
        } catch (var3: NumberFormatException) {
            sOnly = true
        }
        iVal = i ?: 0
    }

    val intValue: Int
        get() {
            return iVal
        }

    val isInt: Boolean
        get() {
            return !sOnly
        }

    val isLabel: Boolean
        get() {
            return sOnly
        }

}