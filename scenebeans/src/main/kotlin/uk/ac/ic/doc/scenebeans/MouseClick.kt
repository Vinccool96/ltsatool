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
package uk.ac.ic.doc.scenebeans

import uk.ac.ic.doc.scenebeans.event.AnimationEvent
import uk.ac.ic.doc.scenebeans.event.AnimationListener

/**
 * The [MouseClick](../../../../../../beans/mouseclick.html)
 * SceneBean.
 */
class MouseClick : InputBase {
    private var _activity_listeners: MutableList<AnimationListener>? = null
    var pressedEvent = "pressed"
    var releasedEvent = "released"

    constructor() : super()
    constructor(sg: SceneGraph) : super(sg)

    fun postMousePressed() {
        postAnimationEvent(pressedEvent)
    }

    fun postMouseReleased() {
        postAnimationEvent(releasedEvent)
    }

    @Synchronized
    fun addAnimationListener(l: AnimationListener) {
        if (_activity_listeners == null) {
            _activity_listeners = ArrayList()
        }
        _activity_listeners!!.add(l)
    }

    @Synchronized
    fun removeAnimationListener(l: AnimationListener) {
        if (_activity_listeners != null) {
            _activity_listeners!!.remove(l)
        }
    }

    @Synchronized
    protected fun postAnimationEvent(activity_name: String) {
        if (_activity_listeners != null) {
            val ev = AnimationEvent(this, activity_name)
            val i: Iterator<*> = _activity_listeners!!.iterator()
            while (i.hasNext()) {
                (i.next() as AnimationListener).animationEvent(ev)
            }
        }
    }

    companion object {
        fun mousePressed(pick_path: List<*>) {
            val i = pick_path.listIterator(pick_path.size)
            while (i.hasPrevious()) {
                val o = i.previous()!!
                if (o is MouseClick) {
                    o.postMousePressed()
                    return
                }
            }
        }

        fun mouseReleased(pick_path: List<*>) {
            val i = pick_path.listIterator(pick_path.size)
            while (i.hasPrevious()) {
                val o = i.previous()!!
                if (o is MouseClick) {
                    o.postMouseReleased()
                    return
                }
            }
        }
    }
}