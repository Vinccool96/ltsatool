package io.github.vinccool96.ltsa.ltsatool.lts

class LTSException : RuntimeException {

    val marker: Any?

    constructor(message: String) : super(message) {
        this.marker = null
    }

    constructor(message: String, marker: Any) : super(message) {
        this.marker = marker
    }

}