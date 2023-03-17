package io.github.vinccool96.ltsa.ltsatool.lts

class Transition {
    var from = 0

    var to = 0

    var event: Symbol? = null

    constructor()

    constructor(var1: Int) {
        from = var1
    }

    constructor(var1: Int, var2: Symbol?, var3: Int) {
        from = var1
        to = var3
        event = var2
    }

    override fun toString(): String {
        return "$from $event $to"
    }
}