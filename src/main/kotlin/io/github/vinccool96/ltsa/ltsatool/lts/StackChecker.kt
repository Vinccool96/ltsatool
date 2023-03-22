package io.github.vinccool96.ltsa.ltsatool.lts

class StackChecker(val coder: StateCodec, val checker: StackCheck) {

    fun onStack(var1: IntArray): Boolean {
        val var2 = coder.encode(var1)
        return if (var2 == null) false else checker.onStack(var2)
    }

}