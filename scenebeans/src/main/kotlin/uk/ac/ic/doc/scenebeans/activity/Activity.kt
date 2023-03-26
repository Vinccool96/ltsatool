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

import uk.ac.ic.doc.scenebeans.event.AnimationEvent
import uk.ac.ic.doc.scenebeans.event.AnimationListener
import java.io.Serializable

/**
 * An Activity performs some simulation in repeated steps.
 */
interface Activity : Serializable {
    /**
     * Returns `true` if this activity is finite, that is if
     * it eventually runs to completion.
     *
     * @return `true` if the activity is finite, `false`
     * otherwise.
     */
    val isFinite: Boolean
    /**
     * Returns the object that is responsible for calling the
     * `performActivity` method of this activity.
     * An activity can be paused by removing it from its activity runner
     * and resumed by adding it to its activity runner again.
     *
     * @return The ActivityRunner of this Activity.
     */
    /**
     * Sets the object that is responsible for calling the
     * `performActivity` method of this activity. This method is
     * called by [ActivityRunner]
     * objects when Activities are added to them.
     *
     * @param r The ActivityRunner that will run this activity.
     * @throws IllegalStateException This activity already has an ActivityRunner
     */
    var activityRunner: ActivityRunner?

    /**
     * An [AnimationEvent] is posted to the
     * event listeners when a finite activity completes or a periodic activity
     * completes one period of simulation.
     *
     * @param l The listener to add.
     */
    fun addAnimationListener(l: AnimationListener)

    /**
     * Removes an animation listener from the activity.
     *
     * @param l The listener to remove.
     */
    fun removeAnimationListener(l: AnimationListener)

    /**
     * Resets the activity so that it restarts its behaviour from its
     * initial state.
     */
    fun reset()

    /**
     * Called periodically to perform the activity's behaviour.
     *
     *
     * The duration of the animation frame, in seconds.
     */
    fun performActivity(secs: Double)
}