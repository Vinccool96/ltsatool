package io.github.vinccool96.ltsa.ltsatool.lts

class Counter(var count: Int) {

    val label: Int
        get() {
            return count++
        }

    val lastLabel: Int
        get() {
            return count
        }

    fun interval(var1: Int): Int {
        val var2 = count
        count += var1
        return var2
    }

}