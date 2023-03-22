package io.github.vinccool96.ltsa.ltsatool.lts

import java.util.*

interface Animator {

    fun initialise(var1: Vector<String>): BitSet

    val menuNames: Array<String>

    val allNames: Array<String>

    fun menuStep(var1: Int): BitSet?

    fun singleStep(): BitSet?

    fun actionChosen(): Int

    fun actionNameChosen(): String

    val isError: Boolean

    val isEnd: Boolean

    fun nonMenuChoice(): Boolean

    val priorityActions: BitSet?

    val priority: Boolean

    fun message(var1: String)

    val hasErrorTrace: Boolean

    fun traceChoice(): Boolean

    fun traceStep(): BitSet?

}