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
import uk.ac.ic.doc.scenebeans.PointBehaviourListener
import java.awt.geom.Point2D
import java.io.Serializable

/**
 * The [MovePoint](../../../../../../../beans/movepoint.html)
 * behaviour bean.
 */
class MovePoint : PointActivityBase, Serializable {
    private var _from: Point2D
    private var _to: Point2D
    private var _x_len = 0.0
    private var _y_len = 0.0
    private var _duration: Double
    private var _timeout: Double

    constructor() {
        _from = Point2D.Double(0.0, 0.0)
        _to = Point2D.Double(0.0, 0.0)
        _timeout = 1.0
        _duration = _timeout
        setDistances()
    }

    constructor(from: Point2D, to: Point2D, t: Double) {
        _from = from
        _to = to
        _timeout = t
        _duration = _timeout
        setDistances()
    }

    var from: Point2D
        get() = _from
        set(p) {
            _from = p
            setDistances()
        }
    var to: Point2D
        get() = _to
        set(p) {
            _to = p
            setDistances()
        }
    var duration: Double
        get() = _duration
        set(v) {
            _timeout = v
            _duration = _timeout
        }
    val value: Point2D
        get() {
            val ratio = 1.0 - _timeout / _duration
            val x = _from.x + ratio * _x_len
            val y = _from.y + ratio * _y_len
            return Point2D.Double(x, y)
        }
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
                postActivityComplete()
            }
            postUpdate(value)
        }
    }

    private fun setDistances() {
        _x_len = _to.x - _from.x
        _y_len = _to.y - _from.y
    }

    fun newFromAdapter(): PointBehaviourListener {
        return FromAdapter()
    }

    fun newToAdapter(): PointBehaviourListener {
        return ToAdapter()
    }

    fun newDurationAdapter(): DoubleBehaviourListener {
        return DurationAdapter()
    }

    internal inner class FromAdapter : PointBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Point2D) {
            from = v
        }
    }

    internal inner class ToAdapter : PointBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Point2D) {
            to = v
        }
    }

    internal inner class DurationAdapter : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            duration = v
        }
    }
}