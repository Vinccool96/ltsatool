/**
 * The Regent Distributed Programming Environment
 *
 *
 * by Nat Pryce, 1998
 */
package uk.ac.ic.doc.natutil

interface Predicate {
    fun evaluate(o: Any?): Boolean
}