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

import java.awt.Graphics2D
import java.awt.Shape
import java.awt.geom.Line2D
import java.awt.geom.Point2D
import java.io.Serializable

/**
 * The [Line](../../../../../../beans/line.html)
 * SceneBean.
 */
class Line : SceneGraphBase, Primitive {
    private var _x0: Double
    private var _y0: Double
    private var _x1: Double
    private var _y1: Double
    override var lastDrawnShape: Shape? = null
        private set

    constructor() {
        _x0 = 0.0
        _y0 = 0.0
        _x1 = 1.0
        _y1 = 1.0
    }

    constructor(start: Point2D, end: Point2D) {
        _x0 = start.x
        _y0 = start.y
        _x1 = end.x
        _y1 = end.y
    }

    constructor(x0: Double, y0: Double, x1: Double, y1: Double) {
        _x0 = x0
        _y0 = y0
        _x1 = x1
        _y1 = y1
    }

    override fun getShape(g: Graphics2D): Shape? {
        return Line2D.Double(_x0, _y0, _x1, _y1)
    }

    var start: Point2D
        get() = Point2D.Double(_x0, _y0)
        set(p) {
            _x0 = p.x
            _y0 = p.y
            isDirty = true
        }
    var end: Point2D
        get() = Point2D.Double(_x1, _y1)
        set(p) {
            _x1 = p.x
            _y1 = p.y
            isDirty = true
        }
    var startX: Double
        get() = _x0
        set(v) {
            _x0 = v
            isDirty = true
        }
    var startY: Double
        get() = _y0
        set(v) {
            _y0 = v
            isDirty = true
        }
    var endX: Double
        get() = _x1
        set(v) {
            _x1 = v
            isDirty = true
        }
    var endY: Double
        get() = _y1
        set(v) {
            _y1 = v
            isDirty = true
        }

    override fun accept(p: SceneGraphProcessor) {
        p.process(this as Primitive)
    }

    override fun draw(g: Graphics2D) {
        val s = getShape(g)
        g.draw(s)
        lastDrawnShape = s
        isDirty = false
    }

    fun newStartAdapter(): Start {
        return Start()
    }

    fun newEndAdapter(): End {
        return End()
    }

    fun newStartXAdapter(): StartX {
        return StartX()
    }

    fun newStartYAdapter(): StartY {
        return StartY()
    }

    fun newEndXAdapter(): EndX {
        return EndX()
    }

    fun newEndYAdapter(): EndY {
        return EndY()
    }

    inner class Start : PointBehaviourListener, Serializable {
        override fun behaviourUpdated(p: Point2D) {
            start = p
        }
    }

    inner class End : PointBehaviourListener, Serializable {
        override fun behaviourUpdated(p: Point2D) {
            end = p
        }
    }

    inner class StartX : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            startX = v
        }
    }

    inner class StartY : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            startY = v
        }
    }

    inner class EndX : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            endX = v
        }
    }

    inner class EndY : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            endY = v
        }
    }
}