package io.github.vinccool96.ltsa.ltsatool.lts

import java.util.*


class Alphabet {

    lateinit var root: PrefixTree

    var myAlpha: Array<String>

    var sm: CompactState? = null

    var maxLevel = 0

    constructor(var1: CompactState) {
        maxLevel = 0
        sm = var1
        myAlpha = var1.alphabet.copyOf()
        this.sort(myAlpha, 1)
        var var2 = 1
        if (var2 < myAlpha.size) {
            this.root = PrefixTree.addName(null, myAlpha[var2])
            ++var2
        }
        while (var2 < myAlpha.size) {
            this.root = PrefixTree.addName(this.root, myAlpha[var2])
            ++var2
        }
        if (!this::root.isInitialized) {
            maxLevel = this.root.maxDepth()
        }
    }

    constructor(var1: Array<String>) {
        maxLevel = 0
        myAlpha = arrayOf()
        val var2 = var1.copyOf()
        if (var2.size > 1) {
            this.sort(var2, 0)
        }
        var var3 = 0
        if (var3 < var2.size) {
            this.root = PrefixTree.addName(null, var2[var3])
            ++var3
        }
        while (var3 < var2.size) {
            this.root = PrefixTree.addName(this.root, var2[var3])
            ++var3
        }
    }

    constructor(var1: Vector<String>) : this(var1.toTypedArray())

    fun print(var1: LTSOutput, var2: Int) {
        var1.outln("Process:\n\t" + sm!!.name)
        var1.outln("Alphabet:")
        if (!this::root.isInitialized) {
            var1.outln("\t{}")
        } else {
            if (var2 == 0) {
                var1.outln("\t${this.root}")
            } else {
                var1.out("\t{ ")
                val var3 = Vector<String>()
                this.root.getStrings(var3, var2 - 1, null as String?)
                val var4: Enumeration<*> = var3.elements()
                var var5 = true
                while (var4.hasMoreElements()) {
                    val var6 = var4.nextElement() as String
                    if (!var5) {
                        var1.out("\t  ")
                    }
                    if (var4.hasMoreElements()) {
                        var1.outln("$var6,")
                    } else {
                        var1.outln(var6)
                    }
                    var5 = false
                }
                var1.outln("\t}")
            }
        }
    }

    private fun sort(var1: Array<String>, var2: Int) {
        for (var4 in var2 until var1.size - 1) {
            var var3 = var4
            for (var5 in var4 + 1 until var1.size) {
                if (var1[var5] < var1[var3]) {
                    var3 = var5
                }
            }
            val var6 = var1[var4]
            var1[var4] = var1[var3]
            var1[var3] = var6
        }
    }

    override fun toString(): String {
        return if (!this::root.isInitialized) "{}" else this.root.toString()
    }

}