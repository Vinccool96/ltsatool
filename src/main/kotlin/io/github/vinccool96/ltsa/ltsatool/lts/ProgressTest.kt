package io.github.vinccool96.ltsa.ltsatool.lts

import java.util.*

class ProgressTest(var name: String?, var pactions: Vector<String>?, var cactions: Vector<String>?) {

    var pset: BitSet? = null

    var cset: BitSet? = null

    init {
        tests!!.addElement(this)
    }

    companion object {

        var tests: Vector<ProgressTest>? = null

        fun init() {
            tests = Vector()
        }

        fun initTests(var0: Array<String>) {
            if (tests != null && tests!!.size != 0) {
                val var1 = Hashtable<String, Int>(var0.size)
                for (var2 in var0.indices) {
                    var1[var0[var2]] = var2
                }
                var var3: ProgressTest
                val var4 = tests!!.elements()
                while (var4.hasMoreElements()) {
                    var3 = var4.nextElement()
                    var3.pset = alphaToBit(var3.pactions, var1)
                    var3.cset = alphaToBit(var3.cactions, var1)
                }
            }
        }

        fun noTests(): Boolean {
            return tests == null || tests!!.size == 0
        }

        private fun alphaToBit(var0: Vector<String>?, var1: Hashtable<String, Int>): BitSet? {
            return if (var0 == null) {
                null
            } else {
                val var2 = BitSet(var1.size)
                val var3 = var0.elements()
                while (var3.hasMoreElements()) {
                    val var4 = var3.nextElement()
                    val var5 = var1[var4]
                    if (var5 != null) {
                        var2.set(var5)
                    }
                }
                var2
            }
        }

    }

}