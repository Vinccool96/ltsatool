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
 * The [Circle](../../../../../../beans/circle.html) SceneBean.
 */
class Circle : PrimitiveBase {
    private var _radius: Double

    constructor() {
        _radius = 1.0
    }

    constructor(r: Double) {
        _radius = r
    }

    override fun getShape(g: Graphics2D): Shape {
        return Ellipse2D.Double(-_radius, -_radius, _radius * 2, _radius * 2)
    }

    var radius: Double
        get() = _radius
        set(r) {
            _radius = r
            isDirty = true
        }

    fun newRadiusAdapter(): Radius {
        return Radius()
    }

    inner class Radius : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(new_value: Double) {
            radius = new_value
        }
    }
}