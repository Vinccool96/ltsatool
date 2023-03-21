package io.github.vinccool96.ltsa.ltsatool.lts

class LTSEvent(var kind: Int, var info: Any?, var name: String?) {

    constructor(kind: Int, info: Any?) : this(kind, info, null)

}