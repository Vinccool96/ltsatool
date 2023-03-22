package io.github.vinccool96.ltsa.ltsatool.lts

class MyHashQueueEntry(var key: ByteArray, var action: Int, var parent: MyHashQueueEntry?) {

    var level = 0

    var next: MyHashQueueEntry? = null

    var link: MyHashQueueEntry? = null

    constructor(key: ByteArray) : this(key, 0, null)

}