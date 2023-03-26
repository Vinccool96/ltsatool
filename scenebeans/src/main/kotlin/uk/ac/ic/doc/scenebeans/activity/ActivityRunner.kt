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
 * An ActivityRunner is responsible for animating activities.
 */
interface ActivityRunner {
    /**
     * Adds an Activity to this runner.  The runner will periodically
     * call it's `performActivity` method.
     *
     * @throws IllegalArgumentException The activity added cannot be handled by this object.  For example,
     * some ActivityRunners might only be able to execute finite activities.
     */
    fun addActivity(a: Activity)

    /**
     * Removes an Activity from this runner.  The runner will no longer
     * call it's `performActivity` method.
     */
    fun removeActivity(a: Activity)
}