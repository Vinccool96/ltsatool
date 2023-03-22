package io.github.vinccool96.ltsa.ltsatool.lts

import java.util.*

interface Automata {

    val alphabet: Array<String>

    fun getTransitions(var1: ByteArray?): MyList

    val violatedProperty: String?

    fun getTraceToState(var1: ByteArray, var2: ByteArray): Vector<String>?

    fun END(var1: ByteArray): Boolean

    fun isAccepting(var1: ByteArray): Boolean

    fun START(): ByteArray

    fun setStackChecker(var1: StackCheck)

    val isPartialOrder: Boolean

    fun disablePartialOrder()

    fun enablePartialOrder()

}