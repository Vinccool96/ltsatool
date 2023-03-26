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
import java.awt.geom.Rectangle2D
import java.io.Serializable

/**
 * The [Rectangle](../../../../../../beans/rectangle.html)
 * SceneBean.
 */
class Rectangle @JvmOverloads constructor(private var _x: Double = 0.0, private var _y: Double = 0.0,
        private var _w: Double = 1.0, private var _h: Double = 1.0) : PrimitiveBase() {
    override fun getShape(g: Graphics2D): Shape? {
        return Rectangle2D.Double(_x, _y, _w, _h)
    }

    var x: Double
        get() = _x
        set(x) {
            _x = x
            isDirty = true
        }
    var y: Double
        get() = _y
        set(y) {
            _y = y
            isDirty = true
        }
    var width: Double
        get() = _w
        set(v) {
            _w = v
            isDirty = true
        }
    var height: Double
        get() = _h
        set(v) {
            _h = v
            isDirty = true
        }

    fun newXAdapter(): XAdapter {
        return XAdapter()
    }

    fun newYAdapter(): YAdapter {
        return YAdapter()
    }

    fun newWidthAdapter(): WidthAdapter {
        return WidthAdapter()
    }

    fun newHeightAdapter(): HeightAdapter {
        return HeightAdapter()
    }

    inner class XAdapter : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            x = v
        }
    }

    inner class YAdapter : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            y = v
        }
    }

    inner class WidthAdapter : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            width = v
        }
    }

    inner class HeightAdapter : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            height = v
        }
    }
}