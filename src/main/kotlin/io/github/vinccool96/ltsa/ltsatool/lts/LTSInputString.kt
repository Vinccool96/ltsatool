package io.github.vinccool96.ltsa.ltsatool.lts

class LTSInputString(private val fSrc: String) : LTSInput {

    private var fPos: Int = -1

    override fun nextChar(): Char {
        ++fPos
        return if (fPos < fSrc.length) fSrc[fPos] else '\u0000'
    }

    override fun backChar(): Char {
        --fPos
        return if (fPos < 0) {
            fPos = 0
            '\u0000'
        } else {
            fSrc[fPos]
        }
    }

    override val marker: Int
        get() {
            return fPos
        }

}