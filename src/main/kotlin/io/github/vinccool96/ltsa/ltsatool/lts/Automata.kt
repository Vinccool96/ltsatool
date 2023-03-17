package io.github.vinccool96.ltsa.ltsatool.lts

import java.util.*

interface Automata {

    fun getAlphabet(): Array<String>

    fun getTransitions(var1: ByteArray): MyList

    fun getViolatedProperty(): String?

    fun getTraceToState(var1: ByteArray, var2: ByteArray): Vector

    fun END(var1: ByteArray): Boolean

    fun isAccepting(var1: ByteArray): Boolean

    fun START(): ByteArray

    fun setStackChecker(var1: StackCheck)

    fun isPartialOrder(): Boolean

    fun disablePartialOrder()

    fun enablePartialOrder()

}