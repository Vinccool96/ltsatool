/**
 * The Regent Distributed Programming Environment
 *
 *
 * by Nat Pryce, 1998
 */
package uk.ac.ic.doc.natutil

class Assertion private constructor(msg: String) : RuntimeException(msg) {

    private constructor() : this("Assertion failed")

    companion object {

        var DEBUG = java.lang.Boolean.getBoolean("uk.ac.ic.doc.natutil.assert")

        fun check(b: Boolean, str: String) {
            if (DEBUG && !b) {
                throw Assertion(str)
            }
        }

    }
}