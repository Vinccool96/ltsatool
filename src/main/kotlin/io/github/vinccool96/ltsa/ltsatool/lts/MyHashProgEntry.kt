package io.github.vinccool96.ltsa.ltsatool.lts

class MyHashProgEntry(var key: ByteArray, var parent: MyHashProgEntry?) {

    var dfn = 0

    var low = 0

    var isReturn = false

    var isProcessed = false

    var next: MyHashProgEntry? = null

    constructor(key: ByteArray) : this(key, null)

}