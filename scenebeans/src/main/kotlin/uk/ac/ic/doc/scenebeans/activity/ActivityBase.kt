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
 * A partial implementation of the
 * [Activity] interface that
 * manages the relationship between an Activity and its
 * [ActivityRunner].
 * The ActivityBase class also provides methods for announcing
 * [AnimationEvent]s and
 * registering [AnimationListener]s
 * with the activity.
 */
abstract class ActivityBase
/**
 * Initialises the activity so that it does not have an ActivityRunner
 * and has no listeners.
 */
protected constructor() : Activity {

    private var _runner: ActivityRunner? = null

    private var _animation_listeners: MutableList<AnimationListener>? = null

    override var activityRunner: ActivityRunner?
        get() = _runner
        set(r) {
            if (_runner != null) {
                check(r == null) { "activity already has a runner" }
            }
            _runner = r
        }

    @Synchronized
    override fun addAnimationListener(l: AnimationListener) {
        if (_animation_listeners == null) {
            _animation_listeners = ArrayList()
        }
        _animation_listeners!!.add(l)
    }

    @Synchronized
    override fun removeAnimationListener(l: AnimationListener) {
        if (_animation_listeners != null) {
            _animation_listeners!!.remove(l)
        }
    }

    /**
     * Posts an [AnimationEvent] to all
     * registered listeners.
     *
     * @param event_name The name of the ActivityEvent posted.
     */
    @Synchronized
    protected fun postActivityComplete(event_name: String) {
        if (_animation_listeners != null) {
            val ev = AnimationEvent(this, event_name)
            val i: Iterator<*> = _animation_listeners!!.iterator()
            while (i.hasNext()) {
                (i.next() as AnimationListener).animationEvent(ev)
            }
        }
    }
}