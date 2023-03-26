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

import java.awt.geom.AffineTransform
import java.io.Serializable

/**
 * The [Shear](../../../../../../beans/shear.html)
 * SceneBean.
 */
class Shear : TransformBase {
    private var _x: Double
    private var _y: Double

    constructor() : super() {
        _x = 0.0
        _y = 0.0
    }

    constructor(x: Double, y: Double, g: SceneGraph?) : super(g) {
        _x = x
        _y = y
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
    override val transform: AffineTransform
        get() = AffineTransform.getShearInstance(_x, _y)

    fun newXAdapter(): X {
        return X()
    }

    fun newYAdapter(): Y {
        return Y()
    }

    inner class X : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            x = v
        }
    }

    inner class Y : DoubleBehaviourListener, Serializable {
        override fun behaviourUpdated(v: Double) {
            y = v
        }
    }
}