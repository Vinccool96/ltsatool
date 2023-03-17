package io.github.vinccool96.ltsa.ltsatool.lts

import java.util.*

object SymbolTable {

    private val keyword: Hashtable<String, Int> = Hashtable(
            hashMapOf("const" to 1, "property" to 2, "range" to 3, "if" to 4, "then" to 5, "else" to 6, "forall" to 7,
                    "when" to 8, "set" to 9, "progress" to 10, "menu" to 11, "animation" to 12, "actions" to 13,
                    "controls" to 14, "deterministic" to 15, "minimal" to 16, "compose" to 17, "target" to 18,
                    "import" to 19, "assert" to 21, "fluent" to 22, "exists" to 24, "rigid" to 25, "fluent" to 22,
                    "constraint" to 26, "initially" to 27))

    operator fun get(var0: String?): Any? {
        return keyword[var0]
    }

}
