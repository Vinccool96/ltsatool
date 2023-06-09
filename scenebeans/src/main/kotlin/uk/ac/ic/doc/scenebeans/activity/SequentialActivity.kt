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
 * A SequentialActivity that runs multiple sub-activities sequentially.
 * A ConcurrentActivity is finite - it completes when all its last sub-activity
 * complete - and can only run activities that are themselves finite.
 */
class SequentialActivity : CompositeActivity(), AnimationListener {
    var _current = 0
    private val _activities: MutableList<Activity> = ArrayList()

    @Synchronized
    override fun reset() {
        _current = 0

        /*  Reset sub-activities in reverse order so that the last activity
         *  to be reset updates its listeners to the initial state of the first
         *  activity to be performed.
         */for (i in _activities.indices.reversed()) {
            _activities[i].reset()
        }
    }

    @Synchronized
    override fun addActivity(a: Activity) {
        require(a.isFinite) { "infinite activity added to sequence" }
        a.activityRunner = this
        a.addAnimationListener(this)
        _activities.add(a)
    }

    @Synchronized
    override fun removeActivity(a: Activity) {
        a.activityRunner = null
        _activities.remove(a)
    }

    @Synchronized
    override fun performActivity(t: Double) {
        if (_current < _activities.size) {
            _activities[_current].performActivity(t)
        }
    }

    override fun animationEvent(ev: AnimationEvent) {
        _current++
        if (_current == _activities.size) {
            postActivityComplete()
        }
    }
}