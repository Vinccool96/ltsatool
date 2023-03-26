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
 * The
 * [ConstantSpeedMove](../../../../../../../beans/constmove.html)
 * behaviour bean.
 */
class ConstantSpeedMove : DoubleActivityBase, Serializable {
    private var _from: Double
    private var _to: Double
    private var _speed: Double
    private var _timeout: Double

    constructor() {
        _from = 0.0
        _to = 0.0
        _speed = 0.01
        _timeout = 1.0
    }

    constructor(from: Double, to: Double, t: Double) {
        _to = to
        _from = from
        _speed = t
        _timeout = duration()
    }

    var from: Double
        get() = _from
        set(v) {
            _from = v
            _timeout = duration()
        }
    var to: Double
        get() = _to
        set(v) {
            _to = v
            _timeout = duration()
        }
    var speed: Double
        get() = _speed
        set(v) {
            _speed = v
            _timeout = duration()
        }
    val value: Double
        get() = _from + (1.0 - _timeout / duration()) * (_to - _from)
    override val isFinite: Boolean
        get() = true

    override fun reset() {
        _from = value
        _timeout = duration()
        postUpdate(value)
    }

    override fun performActivity(t: Double) {
        if (_timeout > 0.0) {
            _timeout -= t
            if (_timeout <= 0.0) {
                _timeout = 0.0
                _from = _to //update at end of move
                postActivityComplete()
            }
            postUpdate(value)
        }
    }

    private fun duration(): Double {
        return Math.max(_speed * Math.abs(_to - _from), 0.001)
    }

    fun newFromAdapter(): DoubleBehaviourListener {
        return FromAdapter()
    }

    fun newToAdapter(): DoubleBehaviourListener {
        return ToAdapter()
    }

    fun newSpeedAdapter(): DoubleBehaviourListener {
        return SpeedAdapter()
    }

    internal inner class FromAdapter : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            from = v
        }
    }

    internal inner class ToAdapter : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            to = v
        }
    }

    internal inner class SpeedAdapter : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            speed = v
        }
    }
}