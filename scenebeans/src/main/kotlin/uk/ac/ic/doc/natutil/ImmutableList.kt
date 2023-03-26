/**
 * The Regent Distributed Programming Environment
 *
 *
 * by Nat Pryce, 1998
 */
package uk.ac.ic.doc.natutil

import java.util.*

abstract class ImmutableList {
    /**
     * Adds an element to the head of the list, returning the new list.
     *
     * @param o The element to be added to the list.
     * @return The list consisting of the element <var>o</var> followed by
     * this list.
     */
    fun add(o: Any): ImmutableList {
        return Node(o, this)
    }

    /**
     * Removes the element <var>o</var> resulting in a new list which
     * is returned to the caller.
     *
     * @param o The object to be removed from the list.
     * @return A list consisting of this list with object <var>o</var> removed.
     */
    abstract fun remove(o: Any): ImmutableList

    /**
     * Removes all elements for which the predicate <var>p</var> returns
     * true, resulting in a new list which is returned to the caller.
     *
     * @param p A predicate that returns `true` if the element is
     * to be removed from the list, and `false` otherwise.
     * @return A list consisting of this list with all elements for which the
     * predicate <var>p</var> returned true removed.
     */
    abstract fun removeIf(p: Predicate): ImmutableList

    /**
     * Applies the procedure <var>proc</var> to all elements in the list.
     */
    abstract fun forAll(proc: Procedure)

    /**
     * Creates a new list whose elements are the result of applying function
     * <var>fn</var> to the elements of this list.
     */
    abstract fun map(fn: Function): ImmutableList

    /**
     * Returns a "standard" enumeration over the elements of the list.
     */
    fun elements(): Enumeration<*> {
        return object : Enumeration<Any?> {
            private var _current = this@ImmutableList
            override fun hasMoreElements(): Boolean {
                return _current !== EMPTY
            }

            override fun nextElement(): Any {
                val result = (_current as Node)._element
                _current = (_current as Node)._next
                return result
            }
        }
    }

    internal class Node : ImmutableList {
        var _element: Any
        var _next: ImmutableList

        constructor(element: Any, next: ImmutableList) {
            _element = element
            _next = next
        }

        constructor(element: Any) {
            _element = element
            _next = EMPTY
        }

        override fun removeIf(p: Predicate): ImmutableList {
            val n = _next.remove(p)
            return if (p.evaluate(_element)) {
                n
            } else if (n === _next) {
                this
            } else {
                Node(_element, n)
            }
        }

        override fun remove(old: Any): ImmutableList {
            return if (_element === old) {
                _next
            } else {
                val n = _next.remove(old)
                if (n === _next) {
                    this
                } else {
                    Node(_element, n)
                }
            }
        }

        override fun forAll(proc: Procedure) {
            proc.execute(_element)
            _next.forAll(proc)
        }

        override fun map(fn: Function): ImmutableList {
            return Node(fn.evaluate(_element)!!, _next.map(fn))
        }
    }

    companion object {
        /**
         * The empty list.  Variables of type ImmutableList should be
         * initialised to this value to create new empty lists.
         */
        val EMPTY: ImmutableList = object : ImmutableList() {
            override fun removeIf(p: Predicate): ImmutableList {
                return this
            }

            override fun remove(o: Any): ImmutableList {
                return this
            }

            override fun forAll(proc: Procedure) {
                return
            }

            override fun map(fn: Function): ImmutableList {
                return this
            }
        }
    }
}