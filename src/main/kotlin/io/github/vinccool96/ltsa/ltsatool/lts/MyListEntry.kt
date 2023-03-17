package io.github.vinccool96.ltsa.ltsatool.lts

class MyListEntry(val fromState: Int, val toState: ByteArray, val actionNo: Int) {

    var next: MyListEntry? = null

}
