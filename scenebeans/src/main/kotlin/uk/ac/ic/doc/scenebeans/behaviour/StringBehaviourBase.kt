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
package uk.ac.ic.doc.scenebeans.behaviour

import uk.ac.ic.doc.scenebeans.StringBehaviour
import uk.ac.ic.doc.scenebeans.StringBehaviourListener
import java.io.Serializable

/**
 * Base class for [StringBehaviour] beans.
 * Manages the list of behaviour listeners and provides subclasses with
 * a method with which they can announce behaviour updates.
 */
abstract class StringBehaviourBase : StringBehaviour, Serializable {
    private var _behaviour_listeners: MutableList<StringBehaviourListener>

    /**
     * Constructs a StringBehaviourBase.
     */
    protected constructor() {
        _behaviour_listeners = ArrayList()
    }

    /**
     * Constructs a StringBehaviourBase with a specific list.  This allows
     * derived classes to specify the type of list used to hold listener
     * references.
     *
     * @param l The list to hold listener references.
     */
    protected constructor(l: MutableList<StringBehaviourListener>) {
        _behaviour_listeners = l
    }

    /**
     * Adds a listener to the behaviour.
     *
     * @param l The listener to add.
     */
    @Synchronized
    override fun addStringBehaviourListener(l: StringBehaviourListener) {
        _behaviour_listeners.add(l)
    }

    /**
     * Removes a listener from the behaviour.
     *
     * @param l The listener to remove.
     */
    @Synchronized
    override fun removeStringBehaviourListener(l: StringBehaviourListener) {
        _behaviour_listeners.remove(l)
    }

    /**
     * Announces an update of the behaviour's value to all registered listeners.
     *
     * @param v The new value of the behaviour.
     */
    @Synchronized
    protected fun postUpdate(v: String?) {
        val i: Iterator<*> = _behaviour_listeners.iterator()
        while (i.hasNext()) {
            (i.next() as StringBehaviourListener).behaviourUpdated(v)
        }
    }
}