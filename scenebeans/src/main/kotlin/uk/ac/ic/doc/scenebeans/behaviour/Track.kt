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

import java.awt.geom.Point2D
import java.io.Serializable

/**
 * The [Track](../../../../../../../beans/track.html)
 * behaviour bean.
 */
class Track : PointActivityBase, Serializable {
    private var _nodes: DoubleArray
    private var _events: Array<String?>
    private var _timeout = 0.0
    private var _current = 0

    constructor() {
        _nodes = DoubleArray(0)
        _events = arrayOfNulls(0)
    }

    constructor(nodes: Int) {
        _nodes = DoubleArray(3 * nodes - 1)
        _events = arrayOfNulls(nodes - 1)
    }

    @get:Synchronized
    @set:Synchronized
    var pointCount: Int
        get() = (_nodes.size + 1) / 3
        set(n) {
            val new_nodes = DoubleArray(3 * n - 1)
            System.arraycopy(_nodes, 0, new_nodes, 0, Math.min(_nodes.size, new_nodes.size))
            _nodes = new_nodes
            val new_events = arrayOfNulls<String>(n - 1)
            System.arraycopy(_events, 0, new_events, 0, Math.min(_events.size, new_events.size))
            _events = new_events
        }

    @Synchronized
    fun getPoint(n: Int): Point2D {
        var n = n
        n *= 3
        return Point2D.Double(_nodes[n], _nodes[n + 1])
    }

    @Synchronized
    fun setPoint(n: Int, p: Point2D) {
        var n = n
        n *= 3
        _nodes[n] = p.x
        _nodes[n + 1] = p.y
    }

    @Synchronized
    fun getX(n: Int): Double {
        return _nodes[n * 3]
    }

    @Synchronized
    fun setX(n: Int, x: Double) {
        _nodes[n * 3] = x
    }

    @Synchronized
    fun getY(n: Int): Double {
        return _nodes[n * 3 + 1]
    }

    @Synchronized
    fun setY(n: Int, y: Double) {
        _nodes[n * 3 + 1] = y
    }

    @Synchronized
    fun getDuration(n: Int): Double {
        return _nodes[n * 3 + 2]
    }

    @Synchronized
    fun setDuration(n: Int, t: Double) {
        _nodes[n * 3 + 2] = t
    }

    @Synchronized
    fun getEvent(n: Int): String? {
        return _events[n]
    }

    @Synchronized
    fun setEvent(n: Int, event_name: String?) {
        _events[n] = event_name
    }

    @get:Synchronized
    val value: Point2D
        get() = if (hasFinished()) {
            getPoint(pointCount - 1)
        } else {
            val from_x = getX(_current)
            val from_y = getY(_current)
            val to_x = getX(_current + 1)
            val to_y = getY(_current + 1)
            Point2D.Double(from_x + ratio() * (to_x - from_x), from_y + ratio() * (to_y - from_y))
        }
    override val isFinite: Boolean
        get() = true

    @Synchronized
    override fun reset() {
        _current = 0
        _timeout = 0.0
        postUpdate(value)
    }

    @Synchronized
    override fun performActivity(t: Double) {
        _timeout += t
        while (!hasFinished() && _timeout >= getDuration(_current)) {
            _timeout -= getDuration(_current)
            if (_events[_current] != null) {
                postActivityComplete(_events[_current]!!)
            }
            _current++
        }
        if (hasFinished()) {
            postActivityComplete()
        }
        postUpdate(value)
    }

    private fun ratio(): Double {
        val d = getDuration(_current)
        return _timeout / d
    }

    private fun hasFinished(): Boolean {
        return _current >= pointCount - 1
    }
}