package io.github.vinccool96.ltsa.ltsatool.lts

interface LTSInput {

    fun nextChar(): Char

    fun backChar(): Char

    val marker: Int

}