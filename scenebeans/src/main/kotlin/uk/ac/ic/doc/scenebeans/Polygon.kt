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
import java.awt.geom.GeneralPath
import java.awt.geom.Point2D
import java.io.Serializable

/**
 * The [Polygon](../../../../../../beans/polygon.html)
 * SceneBean.
 */
class Polygon : PrimitiveBase {
    private var _coords: FloatArray

    constructor() {
        _coords = FloatArray(0)
    }

    constructor(coords: FloatArray) {
        _coords = coords.clone()
    }

    constructor(coords: DoubleArray) {
        _coords = FloatArray(coords.size)
        for (i in coords.indices) {
            _coords[i] = coords[i].toFloat()
        }
    }

    constructor(points: Int) {
        _coords = FloatArray(2 * points)
    }

    override fun getShape(g: Graphics2D): Shape? {
        val p = GeneralPath(GeneralPath.WIND_NON_ZERO, _coords.size / 2 + 2)
        p.moveTo(_coords[0], _coords[1])
        var i = 2
        while (i < _coords.size) {
            p.lineTo(_coords[i], _coords[i + 1])
            i += 2
        }
        p.closePath()
        return p
    }

    var pointCount: Int
        get() = _coords.size / 2
        set(n) {
            val new_coords = FloatArray(n * 2)
            System.arraycopy(_coords, 0, new_coords, 0, Math.min(n * 2, _coords.size))
            _coords = new_coords
            isDirty = true
        }
    val points: Array<Point2D?>
        get() {
            val points = arrayOfNulls<Point2D>(_coords.size / 2)
            for (i in points.indices) {
                points[i] = Point2D.Float(_coords[2 * i], _coords[2 * i + 1])
            }
            return points
        }

    fun setPoints(points: Array<Point2D>) {
        _coords = FloatArray(points.size * 2)
        for (i in points.indices) {
            _coords[i * 2] = points[i].x.toFloat()
            _coords[i * 2 + 1] = points[i].y.toFloat()
        }
        isDirty = true
    }

    fun getPoints(n: Int): Point2D {
        return Point2D.Float(_coords[2 * n], _coords[2 * n + 1])
    }

    fun setPoints(n: Int, p: Point2D) {
        _coords[2 * n] = p.x.toFloat()
        _coords[2 * n + 1] = p.y.toFloat()
        isDirty = true
    }

    fun getXCoord(n: Int): Double {
        return _coords[2 * n].toDouble()
    }

    fun setXCoord(n: Int, x: Double) {
        _coords[2 * n] = x.toFloat()
        isDirty = true
    }

    fun getYCoord(n: Int): Double {
        return _coords[2 * n + 1].toDouble()
    }

    fun setYCoord(n: Int, y: Double) {
        _coords[2 * n + 1] = y.toFloat()
        isDirty = true
    }

    fun newXCoordAdapter(index: Int): XCoord {
        return XCoord(index)
    }

    fun newYCoordAdapter(index: Int): YCoord {
        return YCoord(index)
    }

    fun newPointsAdapter(index: Int): Points {
        return Points(index)
    }

    inner class XCoord(var _index: Int) : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            setXCoord(_index, v)
        }
    }

    inner class YCoord(var _index: Int) : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            setYCoord(_index, v)
        }
    }

    inner class Points(var _index: Int) : PointBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Point2D) {
            setPoints(_index, v)
        }
    }
}