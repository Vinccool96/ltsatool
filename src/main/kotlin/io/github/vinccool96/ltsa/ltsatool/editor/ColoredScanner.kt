package io.github.vinccool96.ltsa.ltsatool.editor

import io.github.vinccool96.ltsa.ltsatool.lts.LTSInput
import io.github.vinccool96.ltsa.ltsatool.lts.Lex
import io.github.vinccool96.ltsa.ltsatool.lts.Symbol
import java.awt.Color
import javax.swing.text.BadLocationException
import javax.swing.text.Document
import javax.swing.text.Segment


class ColoredScanner(private val doc: Document) : LTSInput {

    private var dPos = -1

    private val dText = Segment()

    private var dOffset = 0

    private val lex = Lex(this, false)

    private var current: Symbol? = null

    init {
        if (doc.length > 0) {
            try {
                doc.getText(0, doc.length, dText)
            } catch (_: BadLocationException) {
            }
        }
    }

    operator fun next() {
        try {
            current = lex.inSym()
        } catch (_: Exception) {
        }
    }

    @Throws(BadLocationException::class)
    fun setRange(var1: Int, var2: Int) {
        doc.getText(var1, var2 - var1, dText)
        dPos = -1
        dOffset = var1
        current = null
    }

    val startOffset: Int
        get() {
            return if (current == null) dOffset + dPos else current!!.startPos + dOffset
        }

    val endOffset: Int
        get() {
            return if (current == null) dOffset + dPos + 1 else current!!.endPos + dOffset + 1
        }

    val color: Color?
        get() {
            return if (current == null) Color.black else current!!.color
        }

    override fun nextChar(): Char {
        ++dPos
        return if (dPos < dText.count) dText.array[dPos] else '\u0000'
    }

    override fun backChar(): Char {
        --dPos
        return if (dPos < 0) {
            dPos = -1
            '\u0000'
        } else {
            dText.array[dPos]
        }
    }

    override val marker: Int
        get() {
            return dPos
        }

}