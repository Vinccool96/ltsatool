package io.github.vinccool96.ltsa.ltsatool.lts

import io.github.vinccool96.ltsa.ltsatool.utils.toArrayOfNotNull
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*

class AutCompactState(var1: Symbol, var2: File) : CompactState() {

    init {
        name = var1.toString()
        var var3: BufferedReader? = null
        try {
            var3 = BufferedReader(FileReader(var2))
        } catch (var13: Exception) {
            Diagnostics.fatal("Error opening file$var13", var1 as Symbol?)
        }
        try {
            val var4 = var3!!.readLine()
            if (var4 == null) {
                Diagnostics.fatal("file is empty", var1 as Symbol?)
            }
            maxStates = this.statesAUTheader(var4)
            states = arrayOfNulls(maxStates)
            val var5 = Hashtable<String, Int>()
            val var6 = Counter(0)
            var5["tau"] = var6.label
            var var7: String
            val var8: Int = this.transitionsAUTheader(var4)
            var var9 = 0
            while (var3.readLine().also { var7 = it } != null) {
                this.parseAUTtransition(var7, var5, var6)
                ++var9
            }
            if (var9 != var8) {
                Diagnostics.fatal("transitions read different from .aut header", var1 as Symbol?)
            }
            val alphabet = arrayOfNulls<String>(var5.size)
            var var11: String
            var var12: Int
            val var10 = var5.keys()
            while (var10.hasMoreElements()) {
                var11 = var10.nextElement()
                var12 = var5[var11]!!
                alphabet[var12] = var11
            }
            this.alphabet = alphabet.toArrayOfNotNull()
        } catch (var14: Exception) {
            Diagnostics.fatal("Error reading/translating file$var14", var1 as Symbol?)
        }
    }

    protected fun statesAUTheader(var1: String): Int {
        val var2 = var1.lastIndexOf(44.toChar())
        val var3 = var1.substring(var2 + 1, var1.indexOf(41.toChar())).trim { it <= ' ' }
        return var3.toInt()
    }

    protected fun transitionsAUTheader(var1: String): Int {
        val var2 = var1.indexOf(44.toChar())
        val var3 = var1.lastIndexOf(44.toChar())
        val var4 = var1.substring(var2 + 1, var3).trim { it <= ' ' }
        return var4.toInt()
    }

    protected fun parseAUTtransition(var1: String, var2: Hashtable<String, Int>, var3: Counter) {
        val var4 = var1.indexOf(40.toChar())
        val var5 = var1.indexOf(44.toChar())
        var var6 = var1.substring(var4 + 1, var5).trim { it <= ' ' }
        val var7 = var6.toInt()
        val var8 = var1.indexOf(44.toChar(), var5 + 1)
        var var9 = var1.substring(var5 + 1, var8).trim { it <= ' ' }
        if (var9[0] == '"') {
            var9 = var9.substring(1, var9.length - 1).trim { it <= ' ' }
        }
        val var10 = var1.indexOf(41.toChar())
        var6 = var1.substring(var8 + 1, var10).trim { it <= ' ' }
        val var11 = var6.toInt()
        var var12 = var2[var9]
        if (var12 == null) {
            var12 = var3.label
            var2[var9] = var12
        }
        states[var7] = EventState.add(states[var7], EventState(var12, var11))
    }

}