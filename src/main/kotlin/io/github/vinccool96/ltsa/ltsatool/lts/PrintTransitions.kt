package io.github.vinccool96.ltsa.ltsatool.lts

import io.github.vinccool96.ltsa.ltsatool.utils.toArrayOfNotNull
import io.github.vinccool96.ltsa.ltsatool.utils.toArrayOfNull

class PrintTransitions(var sm: CompactState) {

    fun print(var1: LTSOutput, var2: Int) {
        var var3 = 0
        var1.outln("Process:")
        var1.outln("\t" + sm.name)
        var1.outln("States:")
        var1.outln("\t" + sm.maxStates)
        var1.outln("Transitions:")
        var1.outln("\t" + sm.name + " = Q0,")
        for (var4 in 0 until sm.maxStates) {
            var1.out("\tQ$var4\t= ")
            var var5 = EventState.transpose(sm.states[var4])
            if (var5 == null) {
                if (var4 == sm.endseq) {
                    var1.out("END")
                } else {
                    var1.out("STOP")
                }
                if (var4 < sm.maxStates - 1) {
                    var1.outln(",")
                } else {
                    var1.outln(".")
                }
            } else {
                var1.out("(")
                while (var5 != null) {
                    ++var3
                    if (var3 > var2) {
                        var1.outln("EXCEEDED MAXPRINT SETTING")
                        return
                    }
                    val var6 = EventState.eventsToNext(var5, sm.alphabet.toArrayOfNull()).toArrayOfNotNull()
                    val var7 = Alphabet(var6)
                    var1.out("$var7 -> ")
                    if (var5.next < 0) {
                        var1.out("ERROR")
                    } else {
                        var1.out("Q" + var5.next)
                    }
                    var5 = var5.list
                    if (var5 == null) {
                        if (var4 < sm.maxStates - 1) {
                            var1.outln("),")
                        } else {
                            var1.outln(").")
                        }
                    } else {
                        var1.out("\n\t\t  |")
                    }
                }
            }
        }
    }

}