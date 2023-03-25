package io.github.vinccool96.ltsa.ltsatool.editor

import javax.swing.text.GapContent
import javax.swing.text.PlainDocument

class ColoredDocument : PlainDocument(GapContent(1024)) {

    val scanner = ColoredScanner(this)

    init {
        this.putProperty("lineLimit", 256)
        this.putProperty("tabSize", 4)
    }

    fun getScannerStart(var1: Int): Int {
        return 0
    }

}