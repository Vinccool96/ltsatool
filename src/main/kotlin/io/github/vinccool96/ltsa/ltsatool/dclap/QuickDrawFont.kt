package io.github.vinccool96.ltsa.ltsatool.dclap

class QuickDrawFont(private val value: Int, private val name: String) {

    fun fontval(var1: String): Int {
        return if (name.equals(var1, true)) value else -1
    }

}