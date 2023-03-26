/**
 * SceneBeans, a Java API for animated 2D graphics.
 *
 *
 * Copyright (C) 2000 Nat Pryce and Imperial College
 *
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
 * USA.
 */
package uk.ac.ic.doc.scenebeans.activity

/**
 * An ActivityList holds Activity objects and allows the concurrent
 * invocation of their activities and modification of the collection.
 */
abstract class ActivityList private constructor() {
    /**
     * Invokes performActivity on the Activity objects in the list.
     */
    abstract fun performActivities(t: Double)

    /**
     * Adds an Activity to the head of the list, returning the new list.
     *
     * @param o The element to be added to the list.
     * @return The list consisting of the element <var>o</var> followed by
     * this list.
     */
    fun add(a: Activity): ActivityList {
        return Node(a, this)
    }

    /**
     * Removes the element <var>o</var> resulting in a new list which
     * is returned to the caller.
     *
     * @param o The object to be removed from the list.
     * @return A list consisting of this list with object <var>o</var> removed.
     */
    abstract fun remove(o: Activity): ActivityList

    /**
     * Returns an [java.util.Enumeration] over the elements of the list.
     */
    operator fun iterator(): Iterator<*> {
        return object : MutableIterator<Any?> {
            private var _current = this@ActivityList
            override fun hasNext(): Boolean {
                return _current !== EMPTY
            }

            override fun next(): Any {
                val result: Any = (_current as Node)._element
                _current = (_current as Node)._next
                return result
            }

            override fun remove() {
                throw UnsupportedOperationException("attempt to remove an elements from an ActivityList")
            }
        }
    }

    private class EmptyActivityList : ActivityList() {
        override fun remove(o: Activity): ActivityList {
            return this
        }

        override fun performActivities(t: Double) { // Do nothing
        }
    }

    /**
     * A non-empty list.
     */
    internal class Node : ActivityList {
        var _element: Activity
        var _next: ActivityList

        constructor(element: Activity, next: ActivityList) {
            _element = element
            _next = next
        }

        constructor(element: Activity) {
            _element = element
            _next = EMPTY
        }

        override fun performActivities(t: Double) {
            _element.performActivity(t)
            _next.performActivities(t)
        }

        override fun remove(old: Activity): ActivityList {
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
    }

    companion object {
        /**
         * The empty list.  Variables of type ActivityList should be initialised
         * to this value to create new empty lists.
         */
        val EMPTY: ActivityList = EmptyActivityList()
    }
}