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
package uk.ac.ic.doc.scenebeans.animation

import uk.ac.ic.doc.scenebeans.event.AnimationEvent
import uk.ac.ic.doc.scenebeans.event.AnimationListener

/**
 * An [AnimationListener] that invokes a
 * [Command]
 * when it receives an [AnimationEvent]
 * with a specific name.
 */
class EventInvoker
/**
 * Constructs an EventInvoker.
 *
 * @param event_name The name of the event that triggers invocation of the command.
 * @param command    The command to be invoked.
 */(
        /**
         * Sets the name of the event that triggers invocation of the command.
         *
         * @param event_name The name of the event.
         */
        var eventName: String,
        /**
         * Sets the command triggered by the event.
         *
         * @param command The command triggered by the event.
         */
        var command: Command?) : AnimationListener {
    /**
     * Returns the name of the event that triggers invocation of the command.
     *
     * @return The name of the event.
     */
    /**
     * Returns the command triggered by the event.
     *
     * @return The command triggered by the event.
     */

    /**
     * Invokes the [Command] if the
     * name of the event is the same as the name passed to the constructor of
     * this EventInvoker.
     *
     * @param ev The animation event received by this object.
     */
    override fun animationEvent(ev: AnimationEvent) {
        if (eventName == ev.name) {
            try {
                command!!.invoke()
            } catch (ex: CommandException) { // Ignore it, there's nothing we can do
            }
        }
    }
}