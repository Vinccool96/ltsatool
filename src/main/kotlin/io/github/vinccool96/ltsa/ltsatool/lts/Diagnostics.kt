package io.github.vinccool96.ltsa.ltsatool.lts

object Diagnostics {

    private var output: LTSOutput? = null

    var warningFlag = true

    var warningsAreErrors = false

    fun init(var0: LTSOutput) {
        output = var0
    }

    @Throws(LTSException::class)
    fun fatal(message: String) {
        throw LTSException(message)
    }

    @Throws(LTSException::class)
    fun fatal(message: String, marker: Any) {
        throw LTSException(message, marker)
    }

    @Throws(LTSException::class)
    fun fatal(message: String, symbol: Symbol?) {
        if (symbol != null) {
            throw LTSException(message, symbol.startPos)
        } else {
            throw LTSException(message)
        }
    }

    fun warning(warningMessage: String, exceptionMessage: String, var2: Symbol?) {
        if (warningsAreErrors) {
            fatal(exceptionMessage, var2)
        } else if (warningFlag) {
            if (output == null) {
                fatal("Diagnostic not initialised")
            }
            output!!.outln("Warning - $warningMessage")
        }
    }

}