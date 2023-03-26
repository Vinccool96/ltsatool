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
 * The [ToMove](../../../../../../../beans/tomove.html)
 * behaviour bean.
 */
class ToMove : DoubleActivityBase, Serializable {
    var from: Double
    var to: Double
    private var _duration: Double
    private var _timeout: Double

    constructor() {
        from = 0.0
        to = 0.0
        _timeout = 1.0
        _duration = _timeout
    }

    constructor(from: Double, to: Double, t: Double) {
        this.to = to
        this.from = from
        _timeout = t
        _duration = _timeout
    }

    var duration: Double
        get() = _duration
        set(v) {
            _timeout = v
            _duration = _timeout
        }
    val value: Double
        get() = from + (1.0 - _timeout / _duration) * (to - from)
    override val isFinite: Boolean
        get() = true

    override fun reset() {
        _timeout = _duration
        postUpdate(value)
    }

    override fun performActivity(t: Double) {
        if (_timeout > 0.0) {
            _timeout -= t
            if (_timeout <= 0.0) {
                _timeout = 0.0
                from = to //update position
                postActivityComplete()
            }
        }
        postUpdate(value)
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
            this._from = v
        }
    }

    internal inner class ToAdapter : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            this._to = v
        }
    }

    internal inner class DurationAdapter : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            duration = v
        }
    }
}