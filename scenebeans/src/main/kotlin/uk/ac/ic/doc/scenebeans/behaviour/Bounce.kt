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

import uk.ac.ic.doc.scenebeans.DoubleBehaviourListener
import java.io.Serializable

/**
 * The [Bounce](../../../../../../../beans/bounce.html)
 * behaviour bean.
 */
class Bounce : DoubleActivityBase, Serializable {
    var from: Double
    var to: Double
    var duration: Double
    private var _timeout: Double
    private var _is_increasing: Boolean

    constructor() {
        _timeout = 0.0
        duration = _timeout
        to = duration
        from = to
        _is_increasing = true
    }

    constructor(from: Double, to: Double, duration: Double) {
        this.from = from
        this.to = to
        this.duration = duration
        _timeout = 0.0
        _is_increasing = true
    }

    val value: Double
        get() = from + ratio() * (to - from)
    override val isFinite: Boolean
        get() = false

    override fun reset() {
        _timeout = 0.0
        postUpdate(value)
    }

    override fun performActivity(t: Double) {
        _timeout += t
        while (_timeout >= duration) {
            _timeout -= duration
            _is_increasing = !_is_increasing
            if (_is_increasing) {
                postActivityComplete()
            }
        }
        postUpdate(value)
    }

    private fun ratio(): Double {
        return if (_is_increasing) {
            _timeout / duration
        } else {
            1.0 - _timeout / duration
        }
    }

    fun newFromAdapter(): DoubleBehaviourListener {
        return FromAdapter()
    }

    fun newToAdapter(): DoubleBehaviourListener {
        return ToAdapter()
    }

    fun newDurationAdapter(): DoubleBehaviourListener {
        return DurationAdapter()
    }

    internal inner class FromAdapter : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            this@Bounce.from = v
        }
    }

    internal inner class ToAdapter : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            this@Bounce.to = v
        }
    }

    internal inner class DurationAdapter : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            this@Bounce.duration = v
        }
    }
}