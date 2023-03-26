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

import uk.ac.ic.doc.scenebeans.behaviour.DoubleBehaviourBase
import uk.ac.ic.doc.scenebeans.behaviour.PointBehaviourBase
import java.awt.geom.AffineTransform
import java.awt.geom.NoninvertibleTransformException
import java.awt.geom.Point2D

/**
 * The [MouseMotion](../../../../../../beans/mousemotion.html)
 * SceneBean.
 */
class MouseMotion : InputBase() {
    var isActive = true
    var isDragged = true
    private val _pos = PositionFacet()
    private val _x = DoubleFacet()
    private val _y = DoubleFacet()
    private val _angle = DoubleFacet()
    val positionFacet: PointBehaviour
        get() = _pos

    fun getxFacet(): DoubleBehaviour {
        return _x
    }

    fun getyFacet(): DoubleBehaviour {
        return _y
    }

    val angleFacet: DoubleBehaviour
        get() = _angle

    fun updatePosition(x: Double, y: Double) {
        var angle = Math.atan(y / x)
        angle = if (x >= 0.0) {
            Math.atan(y / x) - Math.PI / 2
        } else {
            Math.atan(y / x) + Math.PI / 2
        }
        _pos.postUpdate(Point2D.Double(x, y))
        _x.postUpdate(x)
        _y.postUpdate(y)
        _angle.postUpdate(angle)
    }

    private class PositionFacet : PointBehaviourBase() {
        public override fun postUpdate(p: Point2D) {
            super.postUpdate(p)
        }
    }

    private class DoubleFacet : DoubleBehaviourBase() {
        public override fun postUpdate(d: Double) {
            super.postUpdate(d)
        }
    }

    private class TransformFailure internal constructor(override var cause: NoninvertibleTransformException) :
            RuntimeException()

    private class Processor internal constructor(x: Double, y: Double, private val _dragged: Boolean) :
            SceneGraphProcessor {
        private val _transform = AffineTransform()
        private var _point: Point2D

        init {
            _point = Point2D.Double(x, y)
        }

        override fun process(sg: Primitive) {/* This space intentionally left blank */
        }

        override fun process(sg: CompositeNode) {
            for (i in 0 until sg.visibleSubgraphCount) {
                sg.getVisibleSubgraph(i)!!.accept(this)
            }
        }

        override fun process(sg: Transform) {
            val old_point = _point
            _point = try {
                sg.transform.inverseTransform(_point, null)
            } catch (ex: NoninvertibleTransformException) {
                throw TransformFailure(ex)
            }
            sg.transformedGraph!!.accept(this)
            _point = old_point
        }

        override fun process(sg: Style) {
            sg.styledGraph!!.accept(this)
        }

        override fun process(sg: Input) {
            if (sg is MouseMotion) {
                val m = sg
                if (m.isActive && (m.isDragged && _dragged || !m.isDragged)) {
                    val p = _transform.transform(_point, null)
                    m.updatePosition(p.x, p.y)
                }
            }
            sg.sensitiveGraph!!.accept(this)
        }
    }

    companion object {
        @Throws(NoninvertibleTransformException::class)
        fun mouseMoved(sg: SceneGraph, x: Double, y: Double) {
            val p = Processor(x, y, false)
            dispatchMouseMotion(sg, p)
        }

        @Throws(NoninvertibleTransformException::class)
        fun mouseDragged(sg: SceneGraph, x: Double, y: Double) {
            val p = Processor(x, y, true)
            dispatchMouseMotion(sg, p)
        }

        @Throws(NoninvertibleTransformException::class)
        private fun dispatchMouseMotion(sg: SceneGraph, p: Processor) {
            try {
                sg.accept(p)
            } catch (ex: TransformFailure) {
                throw ex.cause
            }
        }
    }
}