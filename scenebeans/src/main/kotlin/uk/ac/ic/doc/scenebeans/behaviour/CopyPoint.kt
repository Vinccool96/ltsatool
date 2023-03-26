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
 * The [CopyPoint](../../../../../../../beans/copypoint.html)
 * behaviour bean.
 */
class CopyPoint : PointActivityBase(), Serializable {
    var point: Point2D
    var offset: Point2D

    init {
        point = Point2D.Double(0.0, 0.0)
        offset = Point2D.Double(0.0, 0.0)
    }

    var x: Double
        get() = point.x
        set(v) {
            point = Point2D.Double(v, point.y)
        }
    var y: Double
        get() = point.y
        set(v) {
            point = Point2D.Double(point.x, v)
        }
    val value: Point2D
        get() = Point2D.Double(point.x + offset.x, point.y + offset.y)
    override val isFinite: Boolean
        get() = false

    override fun reset() {
        postUpdate(value)
    }

    override fun performActivity(t: Double) {
        postUpdate(value)
    }

    fun newXAdapter(): XAdapter {
        return XAdapter()
    }

    fun newYAdapter(): YAdapter {
        return YAdapter()
    }

    fun newPointAdapter(): PointAdapter {
        return PointAdapter()
    }

    fun newOffsetAdapter(): OffsetAdapter {
        return OffsetAdapter()
    }

    inner class PointAdapter : PointBehaviourListener {
        override fun behaviourUpdated(v: Point2D) {
            point = v
        }
    }

    inner class OffsetAdapter : PointBehaviourListener {
        override fun behaviourUpdated(v: Point2D) {
            offset = v
        }
    }

    inner class XAdapter : DoubleBehaviourListener {
        override fun behaviourUpdated(v: Double) {
            x = v
        }
    }

    inner class YAdapter : DoubleBehaviourListener {
        override fun behaviourUpdated(v: Double) {
            y = v
        }
    }
}