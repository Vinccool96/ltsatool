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

/**
 * A CompositeActivity that runs multiple sub-activities concurrently.
 * A ConcurrentActivity is finite - it completes when all its sub-activities
 * complete - and can only run activities that are themselves finite.
 */
class ConcurrentActivity : CompositeActivity(), AnimationListener {
    var _finite_count = 0
    var _complete = 0
    private var _activities: ActivityList? = ActivityList.Companion.EMPTY

    @Synchronized
    override fun reset() {
        _complete = 0
        val i = _activities!!.iterator()
        while (i!!.hasNext()) {
            (i.next() as Activity).reset()
        }
    }

    @Synchronized
    override fun addActivity(a: Activity) {
        a.activityRunner = this
        if (a.isFinite) {
            _finite_count++
            a.addAnimationListener(this)
        }
        _activities = _activities!!.add(a)
    }

    @Synchronized
    override fun removeActivity(a: Activity) {
        if (a.isFinite) {
            _finite_count--
            a.removeAnimationListener(this)
        }
        _activities = _activities!!.remove(a)
        a.activityRunner = null
    }

    override fun performActivity(t: Double) {
        _activities!!.performActivities(t)
    }

    override fun animationEvent(ev: AnimationEvent) {
        _complete++
        if (_complete == _finite_count) {
            postActivityComplete()
        }
    }
}