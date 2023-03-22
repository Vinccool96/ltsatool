package io.github.vinccool96.ltsa.ltsatool.lts

class MyHashEntry(var key: ByteArray, var value: Int) {

    var next: MyHashEntry? = null

    constructor(var1: ByteArray) : this(var1, 0)

}