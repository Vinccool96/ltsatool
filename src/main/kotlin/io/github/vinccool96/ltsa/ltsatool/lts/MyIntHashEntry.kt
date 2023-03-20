package io.github.vinccool96.ltsa.ltsatool.lts

class MyIntHashEntry(var key: Int, var value: Int) {

    var next: MyIntHashEntry? = null

    constructor(key: Int) : this(key, 0)

}