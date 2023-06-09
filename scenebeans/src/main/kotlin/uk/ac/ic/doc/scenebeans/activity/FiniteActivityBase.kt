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
 * The base class of finite activities.
 */
abstract class FiniteActivityBase
/**
 * Constructs a FiniteActivityBase.
 */
protected constructor() : ActivityBase() {
    /**
     * Returns the name of this activity, that is reported in the
     * [uk.ac.ic.doc.scenebeans.event.AnimationEvent]s announced
     * when the activity completes.
     *
     * @return The name of the activity.
     */
    /**
     * Sets the name of this activity, that is reported in the
     * [uk.ac.ic.doc.scenebeans.event.AnimationEvent]s announced
     * when the activity completes.
     *
     * @param name The name of the activity.
     */
    var activityName: String? = null
    override val isFinite: Boolean
        /**
         * Returns `true` indicating that the activity is finite.
         *
         * @param return `true`.
         */
        get() = true

    /**
     * Posts an [uk.ac.ic.doc.scenebeans.event.AnimationEvent] indicating
     * that the activity is complete.  The name of the event is initialised as
     * the name of this activity.
     */
    @Synchronized
    protected fun postActivityComplete() {
        postActivityComplete(activityName!!)
    }
}