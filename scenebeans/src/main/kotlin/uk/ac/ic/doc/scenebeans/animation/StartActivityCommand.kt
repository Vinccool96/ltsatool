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

import uk.ac.ic.doc.scenebeans.activity.*

/**
 * A Command that, when invoked, starts an
 * [Activity] by adding it to an
 * [ActivityRunner]
 */
class StartActivityCommand
/**
 * Constructs a StartActivityCommand.
 *
 * @param a The Activity to start.
 * @param r The ActivityRunner that is to run the Activity.
 */(
        /**
         * Sets the activity that is started by this command.
         *
         * @param a The activity started by this command.
         */
        var activity: Activity,
        /**
         * Sets the ActivityRunner that will manage the activity when it
         * is started.
         *
         * @param ar The ActivityRunner that will manage the activity.
         */
        var activityRunner: ActivityRunner?) : Command {
    /**
     * Returns the activity that is started by this command.
     *
     * @return The activity started by this command.
     */
    /**
     * Returns the ActivityRunner that will manage the activity when it
     * is started.
     *
     * @return The ActivityRunner that will manage the activity.
     */

    @Throws(CommandException::class)
    override fun invoke() {
        activityRunner!!.addActivity(activity)
    }
}