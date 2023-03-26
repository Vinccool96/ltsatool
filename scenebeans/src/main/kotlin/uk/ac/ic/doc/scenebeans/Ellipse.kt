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
import java.awt.geom.Ellipse2D
import java.io.Serializable

/**
 * The [Ellipse](../../../../../../beans/ellipse.html)
 * SceneBean.
 */
class Ellipse : PrimitiveBase {
    private var _x_radius: Double
    private var _y_radius: Double

    constructor() {
        _x_radius = 1.0
        _y_radius = 1.0
    }

    constructor(xr: Double, yr: Double) {
        _x_radius = xr
        _y_radius = yr
    }

    override fun getShape(g: Graphics2D): Shape? {
        return Ellipse2D.Double(-_x_radius, -_y_radius, _x_radius * 2, _y_radius * 2)
    }

    var xRadius: Double
        get() = _x_radius
        set(v) {
            _x_radius = v
            isDirty = true
        }
    var yRadius: Double
        get() = _y_radius
        set(v) {
            _y_radius = v
            isDirty = true
        }

    fun newXRadiusAdapter(): XRadius {
        return XRadius()
    }

    fun newYRadiusAdapter(): YRadius {
        return YRadius()
    }

    inner class XRadius : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            xRadius = v
        }
    }

    inner class YRadius : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            yRadius = v
        }
    }
}