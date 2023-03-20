package io.github.vinccool96.ltsa.ltsatool.lts

class MyHashStackEntry {

    var key: ByteArray

    var stateNumber = -1

    var marked = false

    var next: MyHashStackEntry? = null

    var link: MyHashStackEntry? = null

    constructor(key: ByteArray) {
        this.key = key
    }

    constructor(key: ByteArray, stateNumber: Int) {
        this.key = key
        this.stateNumber = stateNumber
    }

}