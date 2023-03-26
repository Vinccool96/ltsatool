/**
 *
 */
package uk.ac.ic.doc.natutil

import java.io.PrintStream
import java.io.PrintWriter

class ChainedException : Exception {

    override var cause: Throwable?
        private set

    constructor(message: String?, cause: Throwable) : super(message) {
        this.cause = cause
    }

    constructor(message: String?) : super(message) {
        cause = null
    }

    constructor() : super() {
        cause = null
    }

    override val message: String?
        get() {
            val m1 = super.message
            val m2 = cause?.message
            return if (m1 == null && m2 == null) {
                null
            } else if (m1 == null) {
                m2!!
            } else if (m2 == null) {
                m1
            } else {
                "$m1 ($m2)"
            }
        }

    override fun printStackTrace(out: PrintWriter) {
        super.printStackTrace(out)
        if (cause != null) {
            out.println("Caused by")
            cause!!.printStackTrace(out)
        }
    }

    override fun printStackTrace(out: PrintStream) {
        super.printStackTrace(out)
        if (cause != null) {
            out.println("Caused by")
            cause!!.printStackTrace(out)
        }
    }
}